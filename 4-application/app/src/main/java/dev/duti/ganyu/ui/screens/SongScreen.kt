package dev.duti.ganyu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.ui.components.CurrentSongDisplay
import dev.duti.ganyu.ui.components.SongList
import kotlinx.coroutines.launch

@UnstableApi
@Composable
fun MusicPlayerScreen(ctx: MyAppContext, modifier: Modifier) {
    var currentSong by remember { mutableStateOf<SongWithDetails?>(null) }
    var currentSongIndex by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()

    val songs = ctx.getSongs()

    Scaffold(modifier = modifier) { padding ->
        Box(modifier = Modifier.Companion.fillMaxSize()) {
            Column(modifier = Modifier.Companion.fillMaxSize()) {
                SongList(songs, ctx, padding) { idx, fullSong ->
                    currentSong = fullSong
                    currentSongIndex = idx
                    scope.launch { ctx.play(fullSong) }
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.Companion.fillMaxSize()
        ) {
            currentSong?.let { song -> CurrentSongDisplay(song, ctx) }
        }
    }
}
