package dev.duti.ganyu.utils

import android.content.Context
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

data class PyYtDownload(
    val videoId: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val tempFilePath: String
)

class PyModule(ctx: Context) {
    private val pyModule: PyObject
    private val cacheDir: String

    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(ctx))
        }
        val py = Python.getInstance()
        pyModule = py.getModule("main")
        cacheDir = ctx.cacheDir.absolutePath
    }

    fun download(videoId: String): PyYtDownload {

        val info = pyModule.callAttr("download", videoId, cacheDir).asList()
        if (info.size != 5) {
            throw Exception("Invalid info tuple")
        }
        val title = info[0].toString()
        val artist = info[1].toString()
        val album = info[2].toString()
        val duration = info[3].toLong()
        val tempFilePath = info[4].toString()
        return PyYtDownload(videoId, title, artist, album, duration, tempFilePath)
    }
}