package dev.duti.ganyu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.ui.components.CurrentSongDisplay
import dev.duti.ganyu.ui.components.SongList
import kotlinx.coroutines.launch

@UnstableApi
@Composable
fun MusicPlayerScreen(ctx: MyAppContext, modifier: Modifier) {

    ctx.refreshSongList()

    val scope = rememberCoroutineScope()

    Scaffold(modifier = modifier) { padding ->
        Box(modifier = Modifier.Companion.fillMaxSize()) {
            Column(modifier = Modifier.Companion.fillMaxSize()) {
                SongList(ctx.songs.value, ctx, padding) { idx, fullSong ->
                    scope.launch { ctx.play(idx) }
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom, modifier = Modifier.Companion.fillMaxSize()
        ) {
            ctx.currentSong.value?.let { song -> CurrentSongDisplay(song, ctx) }
        }
    }
}
