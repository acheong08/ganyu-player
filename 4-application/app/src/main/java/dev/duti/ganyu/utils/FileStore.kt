package dev.duti.ganyu.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import dev.duti.ganyu.data.SongWithDetails
import java.io.File

const val TAG = "FileStore"

fun getLocalMediaFiles(ctx: Context): List<SongWithDetails> {
    Log.i(TAG, "Start: Getting local media files")
    if (ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.READ_MEDIA_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e(TAG, "MISSING PERMISSION: READ_MEDIA_AUDIO")
        return emptyList()
    }
    val songs = mutableListOf<SongWithDetails>()
    // Projection (columns we want to query)
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.GENRE
    )
    // Filter only music files with valid duration
    val selection =
        "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND " + "${MediaStore.Audio.Media.DURATION} >= 0"
    ctx.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null
    )?.use { cursor ->
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val mediaIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
        while (cursor.moveToNext()) {
            // Extract values from cursor
            val id = cursor.getLong(idCol)
            val title = cursor.getString(titleCol) ?: "Unknown"
            val artistName = cursor.getString(artistCol) ?: "Unknown Artist"
            val albumName = cursor.getString(albumCol) ?: ""
            val duration = cursor.getLong(durationCol)
            var mediaId = cursor.getStringOrNull(mediaIdCol)
            if (mediaId == null) {
                mediaId = ""
            }
            val song = SongWithDetails(id, title, albumName, duration, artistName, mediaId)
            songs.add(song)
            Log.i(TAG, "Found song $title, $mediaId")
        }
    }
    return songs

}

fun saveYtDownload(ctx: Context, ytDownload: PyYtDownload) {
    val tempFile = File(ytDownload.tempFilePath)
    // Create MediaStore entry
    val values = ContentValues().apply {
        put(MediaStore.Audio.Media.DISPLAY_NAME, "${ytDownload.videoId}.m4a")
        put(MediaStore.Audio.Media.MIME_TYPE, "audio/m4a")
        put(MediaStore.Audio.Media.TITLE, ytDownload.title)
        put(MediaStore.Audio.Media.ARTIST, ytDownload.artist)
        put(MediaStore.Audio.Media.ALBUM, ytDownload.album)
        put(MediaStore.Audio.Media.GENRE, ytDownload.videoId)
        put(MediaStore.Audio.Media.IS_PENDING, 1)
        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
    }

    Log.i(TAG, ytDownload.videoId)

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