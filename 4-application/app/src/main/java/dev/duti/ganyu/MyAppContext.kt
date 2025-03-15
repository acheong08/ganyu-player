package dev.duti.ganyu

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dev.duti.ganyu.data.AlbumWithDetails
import dev.duti.ganyu.data.Artist
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.storage.MusicRepository
import dev.duti.ganyu.storage.getLocalMediaFiles
import java.io.File

class MyAppContext(val ctx: Context, val repo: MusicRepository, val player: MediaController) {
    private val pyModule: PyObject
    private var songListCache: List<SongWithDetails>? = null
    val playerState = mutableIntStateOf(Player.STATE_IDLE)
    var currentSong = mutableStateOf<SongWithDetails?>(null)
    var currentSongIndex = mutableIntStateOf(0)

    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(ctx))
        }
        val py = Python.getInstance()
        pyModule = py.getModule("main")
        Log.i("PYTHON", pyModule.callAttr("main").toString())
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                Log.i("PLAYER", "Player state changed $state")
                playerState.intValue = state
            }
        })
    }

    suspend fun download(id: String) {
        try {
            // Use cache directory for temporary file storage
            val cacheDir = ctx.cacheDir.absolutePath

            // Info is now (title, artist, album, temp_file_path)
            val info = pyModule.callAttr("download", id, cacheDir).asList()
            if (info.size != 5) {
                throw Exception("Invalid info tuple")
            }
            val title = info[0].toString()
            val artist = info[1].toString()
            val album = info[2].toString()
            val duration = info[3].toLong()
            val tempFilePath = info[4].toString()
            val tempFile = File(tempFilePath)
            // Create MediaStore entry
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, "$id.m4a")
                put(MediaStore.Audio.Media.MIME_TYPE, "audio/m4a")
                put(MediaStore.Audio.Media.TITLE, title)
                put(MediaStore.Audio.Media.ARTIST, artist)
                put(MediaStore.Audio.Media.ALBUM, album)
                put(MediaStore.Audio.Media.IS_PENDING, 1)
                put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            }

            val uri =
                ctx.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                // Copy the file content from temp file to MediaStore
                ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    tempFile.inputStream().use { inputStream -> inputStream.copyTo(outputStream) }
                }

                // Mark the file as ready (not pending anymore)
                val updatedValues =
                    ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }
                ctx.contentResolver.update(uri, updatedValues, null, null)

                val contentId = ContentUris.parseId(uri)
                val artistDetails = Artist(name = artist)
                val song = SongWithDetails(
                    contentId,
                    title,
                    AlbumWithDetails(album, null, artistDetails),
                    duration,
                    artistDetails
                )

                // Insert song into database
                repo.insertSong(song)

                Log.i("PYTHON", "Downloaded $id to $uri")

                // Delete the temporary file
                tempFile.delete()
            }
        } catch (e: Exception) {
            Log.e("PYTHON", "Download failed", e)
        }
    }

    fun getSongs(): List<SongWithDetails> {
        if (songListCache == null) {
            songListCache = getLocalMediaFiles(ctx)
        }
        return songListCache!!
    }

    fun play(song: SongWithDetails, idx: Int) {
        currentSong.value = song
        currentSongIndex.intValue = idx
        // Get URI via id
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.path)
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
    }
}
