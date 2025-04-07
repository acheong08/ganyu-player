package dev.duti.ganyu

import android.util.Log
import com.dylibso.chicory.experimental.hostmodule.annotations.HostModule
import com.dylibso.chicory.experimental.hostmodule.annotations.WasmExport
import com.dylibso.chicory.runtime.HostFunction
import com.dylibso.chicory.runtime.Memory

@HostModule("wasm")
class WasmHost(ctx: MyAppContext){
    @WasmExport
    fun log(memory: Memory, ptr: Int, len: Int) {
        val text = memory.readString(ptr, len)
        Log.i("WASM", "$text")
    }

    fun toHostFunctions(): Array<HostFunction> {
        return WasmHost_ModuleFactory.toHostFunctions(this)
    }
}