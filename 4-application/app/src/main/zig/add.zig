extern "wasm" fn log(ptr: [*]const u8, len: i32) void;

export fn add(a: i32, b: i32) i32 {
    log("Hello world!", 13);
    return a + b;
}
