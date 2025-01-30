java_path = client/app/src/main/java
go_path = server/
all:
	protoc -I=./client --kotlin_out=$(java_path) --java_out=$(java_path) --go_out=$(go_path) --proto_path="proto" device.proto
