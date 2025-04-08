const wasm_allocator = @import("std").heap.wasm_allocator;

extern "wasm" fn log(ptr: [*]const u8, len: i32) void;
extern "wasm" fn fetch(urlPtr: [*]const u8, urlLen: i32, dataPtr: [*]const u8, dataLen: i32) i64;
extern "wasm" fn download(ptr: [*]const u8, len: i32) void;

fn decode64(i: i64) [2]u32 {
    return .{ @intCast(i >> 32), @intCast(i) };
}

export fn songOfTheDay() void {
    const url = "https://duti.dev/video.txt";
    const resp = decode64(fetch(url, url.len, "", 0));
    const slice = @as([*]const u8, @ptrFromInt(resp[0]))[0..@intCast(resp[1])];
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
