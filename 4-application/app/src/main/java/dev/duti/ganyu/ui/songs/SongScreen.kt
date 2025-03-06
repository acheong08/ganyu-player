package dev.duti.ganyu.ui.songs

import android.content.ContentUris
import android.provider.MediaStore
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.data.Song
import dev.duti.ganyu.data.SongWithDetails

@UnstableApi
@Composable
fun MusicPlayerScreen(ctx: MyAppContext, modifier: Modifier) {
    var currentSong by remember { mutableStateOf<SongWithDetails?>(null) }


    val refreshTrigger = remember { mutableIntStateOf(0) }

    val songs = produceState<List<Song>>(initialValue = emptyList(), refreshTrigger.intValue) {
        ctx.reloadMusicDb()
        ctx.repo.getAllSongs().collect { value = it }
    }



    ctx.player.setMediaItems(songs.value.map { fullSong ->
        MediaItem.fromUri(
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, fullSong.path
            ).toString()
        )
    })

    Scaffold(
        modifier = modifier
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                SongList(songs.value, ctx, padding) { idx, fullSong ->
                    currentSong = fullSong
                    ctx.player.seekTo(idx, 0)
                    ctx.player.prepare()
                }
            }

            // FloatingActionButton positioned at the top-right corner
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, end = 16.dp), // Adjust padding as needed
                contentAlignment = Alignment.TopEnd
            ) {
                FloatingActionButton(onClick = {
                    refreshTrigger.intValue = (refreshTrigger.intValue + 1) % Int.MAX_VALUE
                }) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Refresh list"
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()
        ) {
            currentSong?.let { song ->
                CurrentSongDisplay(song, ctx)
            }
        }
    }
}
