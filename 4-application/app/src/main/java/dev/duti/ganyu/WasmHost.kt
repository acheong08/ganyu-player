package dev.duti.ganyu

import android.util.Log
import com.dylibso.chicory.experimental.hostmodule.annotations.HostModule
import com.dylibso.chicory.experimental.hostmodule.annotations.WasmExport
import com.dylibso.chicory.runtime.ExportFunction
import com.dylibso.chicory.runtime.HostFunction
import com.dylibso.chicory.runtime.Memory

@HostModule("wasm")
class WasmHost(val appCtx: MyAppContext) {
    private var alloc: ExportFunction? = null

    @WasmExport
    fun log(memory: Memory, ptr: Int, len: Int) {
        val text = memory.readString(ptr, len)
        Log.i("WASM", "$text")
    }

    fun setAlloc(f: ExportFunction) {
        alloc = f
    }

    @WasmExport
    fun downloadById(memory: Memory, ptr: Int, len: Int) {
        val videoId = memory.readString(ptr, len)
        appCtx.downloadById(videoId)
    }

    @WasmExport
    fun fetch(memory: Memory, urlPtr: Int, urlLen: Int, dataPtr: Int, dataLen: Int): Long {
        if (alloc == null) {
            return 0
        }
        val url = memory.readString(urlPtr, urlLen)
        val data = memory.readBytes(dataPtr, dataLen)
        val resp = if (dataLen == 0) khttp.get(url = url) else khttp.post(url = url, data = data)
        val ptr = alloc!!.apply(resp.content.size.toLong())[0].toInt()
        memory.write(ptr, resp.content)
        return (ptr.toLong() shl 32) or (resp.content.size.toLong() and 0xffffffffL)
    }

    fun toHostFunctions(): Array<HostFunction> {
        return WasmHost_ModuleFactory.toHostFunctions(this)
    }
}
