package dev.duti.ganyu

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import dev.duti.ganyu.data.ShortVideo
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.data.YoutubeApiClient
import dev.duti.ganyu.storage.SettingsRepository
import dev.duti.ganyu.utils.PyModule
import dev.duti.ganyu.utils.getLocalMediaFiles
import dev.duti.ganyu.utils.saveYtDownload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.Date

const val TAG = "APP_CONTEXT"

class MyAppContext(
    val ctx: Context,
    val player: MediaController,
    val settingsRepository: SettingsRepository
) {
    private var songs = mutableStateOf<List<SongWithDetails>>(listOf())
    var songFilterFunc = mutableStateOf({ song: SongWithDetails -> true })
    private var songSortComparator = mutableStateOf({ song: SongWithDetails -> song.title })
    var filteredSongs = derivedStateOf {
        songs.value.filter { songFilterFunc.value(it) }.sortedBy {
            songSortComparator.value(it)
        }
    }

    val songsMap = derivedStateOf {
        songs.value.associate { it.id to true }
    }

    var currentSong = mutableStateOf<SongWithDetails?>(null)
    private var currentSongIndex = mutableIntStateOf(0)
    private val pyModule = PyModule(ctx)

    var ivIsLoggedIn = mutableStateOf(false)

    val downloading = mutableStateListOf<ShortVideo>()
    private val failedDownloads = mutableStateListOf<ShortVideo>()

    val scope = CoroutineScope(Dispatchers.IO)

    init {
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                currentSong.value = songs.value[player.currentMediaItemIndex]
            }
        })

        refreshSongList()
        scope.launch {
            val ivCookieString = settingsRepository.getCookie()
            if (ivCookieString == null) {
                return@launch
            }
            Log.i(TAG, "Invidious cookies loaded from memory")
            val cookie = Cookie.parse("https://iv.duti.dev".toHttpUrl(), ivCookieString)
            if (cookie == null || cookie.expiresAt < Date().time) {
                Log.i(TAG, "Invidious cookie expired")
                settingsRepository.deleteCookie()
                return@launch
            }
            YoutubeApiClient.setCookies(ivCookieString)
            ivIsLoggedIn.value = true
        }

    }

    suspend fun ivLogin(cookie: String) {
        YoutubeApiClient.setCookies(cookie)
        ivIsLoggedIn.value = true
        settingsRepository.saveIvCookie(cookie)
    }

    fun refreshSongList() {
        scope.launch {
            songs.value = getLocalMediaFiles(ctx)
            withContext(Dispatchers.Main) {
                val mediaItems = songs.value.map { song ->
                    MediaItem.Builder().setUri(
                        ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.path
                        )
                    ).setMediaMetadata(
                        MediaMetadata.Builder().setTitle(song.title)
                            .setArtist(song.artist).build()
                    ).build()
                }
                player.setMediaItems(mediaItems)
                player.prepare()
            }
        }
    }

    fun play(idx: Int) {
        currentSong.value = songs.value[idx]
        currentSongIndex.intValue = idx
        player.seekTo(idx, 0L)
        player.play()
    }

    fun playPrev() {
        var newIndex = player.currentMediaItemIndex - 1
        if (newIndex == -1) {
            newIndex = player.mediaItemCount - 1
        }
        Log.i(TAG, "Playing index: $newIndex with max ${player.mediaItemCount}")
        player.seekTo(newIndex, 0)
    }

    fun playNext() {
        val newIndex = (player.currentMediaItemIndex + 1) % player.mediaItemCount
        Log.i(TAG, "Playing index: $newIndex with max ${player.mediaItemCount}")
        player.seekTo(newIndex, 0)
    }

    fun download(video: ShortVideo) {
        if (songsMap.value.contains(video.videoId) || downloading.contains(video)) {
            Log.i(TAG, "Video already downloaded, skipping.")
            return
        }
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
