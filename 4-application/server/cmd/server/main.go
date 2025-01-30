package main

import (
	"ganyu/proto/device"
	"io"
	"net/http"

	"google.golang.org/protobuf/proto"
)

func main() {
	mux := http.NewServeMux()

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
}
