package dev.duti.ganyu.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.ui.components.CurrentSongDisplay
import dev.duti.ganyu.ui.components.SongList
import kotlinx.coroutines.launch

@UnstableApi
@Composable
fun MusicPlayerScreen(ctx: MyAppContext, modifier: Modifier) {

    val scope = rememberCoroutineScope()
    val songs = ctx.getSongs()

    LaunchedEffect(ctx.currentSong.value, ctx.playerState.intValue) {
        Log.i("PLAYER", "Player state change detected in player screen")
        if (ctx.playerState.intValue == Player.STATE_ENDED) {
            val nextIndex = (ctx.currentSongIndex.intValue + 1) % songs.size
            val nextSong = songs[nextIndex]

            scope.launch { ctx.play(nextSong, nextIndex) }
        }
    }

    Scaffold(modifier = modifier) { padding ->
        Box(modifier = Modifier.Companion.fillMaxSize()) {
            Column(modifier = Modifier.Companion.fillMaxSize()) {
                SongList(songs, ctx, padding) { idx, fullSong ->
                    scope.launch { ctx.play(fullSong, idx) }
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
