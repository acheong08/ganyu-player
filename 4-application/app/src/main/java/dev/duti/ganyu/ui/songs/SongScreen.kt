package dev.duti.ganyu.ui.songs

import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.storage.MusicRepository

@UnstableApi
@Composable
fun MusicPlayerScreen(repo: MusicRepository, player: MediaController, modifier: Modifier) {
    var currentSong by remember { mutableStateOf<SongWithDetails?>(null) }

    val songs = repo.getAllSongs().collectAsState(initial = emptyList())

    player.setMediaItems(songs.value.map { fullSong ->
        MediaItem.fromUri(
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, fullSong.path
            ).toString()
        )
    })

    Scaffold(
        bottomBar = {
            currentSong?.let { song ->
                CurrentSongDisplay(song, player)
            }
        }, modifier = modifier
    ) { padding ->
        SongList(songs.value, repo, padding) { idx, fullSong ->
            currentSong = fullSong
            player.seekTo(idx, 0)
            player.prepare()
        }
    }
}
