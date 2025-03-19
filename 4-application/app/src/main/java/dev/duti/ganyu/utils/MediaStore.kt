package dev.duti.ganyu.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import dev.duti.ganyu.data.AlbumWithDetails
import dev.duti.ganyu.data.Artist
import dev.duti.ganyu.data.SongWithDetails
import java.io.File

fun saveYtDownload(ctx: Context, ytDownload: PyYtDownload) {
    val tempFile = File(ytDownload.tempFilePath)
    // Create MediaStore entry
    val values = ContentValues().apply {
        put(MediaStore.Audio.Media.DISPLAY_NAME, "${ytDownload.videoId}.m4a")
        put(MediaStore.Audio.Media.MIME_TYPE, "audio/m4a")
        put(MediaStore.Audio.Media.TITLE, ytDownload.title)
        put(MediaStore.Audio.Media.ARTIST, ytDownload.artist)
        put(MediaStore.Audio.Media.ALBUM, ytDownload.album)
        put(MediaStore.Audio.Media.IS_PENDING, 1)
        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
    }

    val uri =
        ctx.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
    if (uri == null) {
        throw Exception("Failed to get content resolver URI")
    }
    // Copy the file content from temp file to MediaStore
    ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
        tempFile.inputStream().use { inputStream -> inputStream.copyTo(outputStream) }
    }

    // Mark the file as ready (not pending anymore)
    val updatedValues = ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }
    ctx.contentResolver.update(uri, updatedValues, null, null)


    tempFile.delete()
}