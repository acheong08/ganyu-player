package main

import (
	"context"
	"ganyu/proto/device"
	"io"
	"log"
	"net/http"
	"time"

	"google.golang.org/protobuf/proto"
)

func main() {
	mux := http.NewServeMux()
	chMap := make(map[string]chan string)

	mux.HandleFunc("/upload", func(w http.ResponseWriter, r *http.Request) {
		defer r.Body.Close()
		b, err := io.ReadAll(r.Body)
		if err != nil {
			w.WriteHeader(400)
			return
		}
		// TODO: Replace with generic message
		var contacts device.Contacts
		if err := proto.Unmarshal(b, &contacts); err != nil {
			w.WriteHeader(400)
			return
		}
		w.WriteHeader(200)
		return
	})

	mux.HandleFunc("/send", func(w http.ResponseWriter, r *http.Request) {
		id := r.URL.Query().Get("id")
		if id == "" {
			w.WriteHeader(400)
			return
		}
		ch, ok := chMap[id]
		if !ok {
			w.WriteHeader(404)
			return
		}
		defer r.Body.Close()
		b, err := io.ReadAll(r.Body)
		if err != nil {
			w.WriteHeader(400)
			return
		}
		log.Println(id, string(b))
		ch <- string(b)
	})

	mux.HandleFunc("/poll", func(w http.ResponseWriter, r *http.Request) {
		// Long poll handler, wait for commands
		chId := r.URL.Query().Get("id")
		if chId == "" {
			w.WriteHeader(400)
			return
		}
		log.Println("Polling ", chId)
		ch, ok := chMap[chId]
		if !ok {
			ch = make(chan string)
			chMap[chId] = ch
		}
		ctx, cancel := context.WithTimeout(r.Context(), time.Minute)
		defer cancel()

		select {
		case cmd := <-ch:
			w.WriteHeader(200)
			w.Write([]byte(cmd))
			// With timeout context

		case <-ctx.Done():
			w.WriteHeader(http.StatusNoContent)
		}
	})
	if err := http.ListenAndServe("100.64.0.5:8003", mux); err != nil {
		panic(err)
	}
}
