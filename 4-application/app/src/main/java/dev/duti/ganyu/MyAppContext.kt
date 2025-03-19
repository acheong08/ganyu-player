package dev.duti.ganyu

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import dev.duti.ganyu.data.ShortVideo
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.utils.PyModule
import dev.duti.ganyu.utils.getLocalMediaFiles
import dev.duti.ganyu.utils.saveYtDownload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "APP_CONTEXT"

class MyAppContext(val ctx: Context, val player: MediaController) {
    var songs = mutableStateOf<List<SongWithDetails>>(listOf())
    var currentSong = mutableStateOf<SongWithDetails?>(null)
    private var currentSongIndex = mutableIntStateOf(0)
    private val pyModule = PyModule(ctx)

    var ivLoggedIn = mutableStateOf(false)

    val downloading = mutableStateListOf<ShortVideo>()
    private val failedDownloads = mutableStateListOf<ShortVideo>()

    val scope = CoroutineScope(Dispatchers.IO)

    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    val nextIdx = nextSongIdx()
                    if (nextIdx == -1) {
                        return
                    }
                    play(nextIdx)
                }
            }
        })

        scope.launch {
            songs.value = getLocalMediaFiles(ctx)
        }
    }

    fun refreshSongList() {
        scope.launch {
            songs.value = getLocalMediaFiles(ctx)
        }
    }

    fun nextSongIdx(): Int {
        // TODO: Get settings for different options (e.g. Random, loop)
        return (currentSongIndex.intValue + 1) % songs.value.size
    }

    fun play(idx: Int) {
        currentSong.value = songs.value[idx]
        currentSongIndex.intValue = idx
        // Get URI via id
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong.value!!.path
        )
        player.setMediaItem(
            MediaItem.Builder().setUri(uri).setMediaMetadata(
                MediaMetadata.Builder().setTitle(currentSong.value!!.title)
                    .setArtist(currentSong.value!!.artist.name).build()
            ).build()
        )
        player.prepare()
        player.play()
    }

    fun download(video: ShortVideo) {
        Log.i(TAG, "Youtube download started")
        // Add to download queue
        downloading.add(video)
        try {
            val ytDownload = pyModule.download(video.videoId)
            saveYtDownload(ctx, ytDownload)
        } catch (e: Exception) {
            Log.e(TAG, "Youtube download failed: ${e.toString()}")
            failedDownloads.add(video)
        } finally {
            downloading.remove(video)
        }
    }

}
