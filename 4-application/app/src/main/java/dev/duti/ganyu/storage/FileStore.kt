package dev.duti.ganyu.storage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import dev.duti.ganyu.data.AlbumWithDetails
import dev.duti.ganyu.data.Artist
import dev.duti.ganyu.data.SongWithDetails

fun getLocalMediaFiles(ctx: Context): List<SongWithDetails> {
    Log.i("FileStore", "Looking for local media")
    if (ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.READ_MEDIA_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e("FileStore", "MISSING PERMISSION: READ_MEDIA_AUDIO")
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
        MediaStore.Audio.Media.DATA
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
        val pathCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        while (cursor.moveToNext()) {
            // Extract values from cursor
            val id = cursor.getLong(idCol)
            val title = cursor.getString(titleCol) ?: "Unknown"
            val artistName = cursor.getString(artistCol) ?: "Unknown Artist"
            val albumName = cursor.getString(albumCol) ?: ""
            val duration = cursor.getLong(durationCol)
            val path = cursor.getString(pathCol)
            Log.i("FileStore", "Path: $path")

            val artist = Artist(name = artistName, art = null)
            val album = if (albumName != "") AlbumWithDetails(
                albumName, null, artist = artist
            ) else null
            val song = SongWithDetails(id, title, album, duration, artist)
            songs.add(song)
            Log.i("FileStore", song.toString())
        }
    }
    return songs

}
