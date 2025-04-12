const wasm_allocator = @import("std").heap.wasm_allocator;

extern "wasm" fn log(ptr: [*]const u8, len: i32) void;
extern "wasm" fn fetch(urlPtr: [*]const u8, urlLen: i32, dataPtr: [*]const u8, dataLen: i32) PackedPtr;
extern "wasm" fn download(ptr: [*]const u8, len: i32) void;

const PackedPtr = packed struct(i64) {
    len: u32,
    ptr: [*]const u8,

    fn slice(foo: PackedPtr) []const u8 {
        return foo.ptr[0..foo.len];
    }
};

export fn songOfTheDay() void {
    const url = "https://duti.dev/video.txt";
    const slice = fetch(url, url.len, "", 0).slice();
    log(slice.ptr, @intCast(slice.len));
    download(slice.ptr, @intCast(slice.len));
    dealloc(slice.ptr, @intCast(slice.len));
}

export fn alloc(len: i32) *const u8 {
    const ptr = wasm_allocator.alloc(u8, @intCast(len)) catch unreachable;
    return @ptrCast(ptr);
}

fn dealloc(ptr: [*]const u8, len: i32) void {
    wasm_allocator.free(ptr[0..@intCast(len)]);
}
