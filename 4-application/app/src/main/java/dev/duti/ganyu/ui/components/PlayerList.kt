package dev.duti.ganyu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.R
import dev.duti.ganyu.data.SongWithDetails
import kotlinx.coroutines.delay

@Composable
fun SongItem(song: SongWithDetails, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art placeholder
        Image(
            painter = painterResource(
                R.drawable.ic_launcher_foreground
            ), // Replace with actual artwork
            contentDescription = "Album art",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}

@Composable
fun SongList(
    songs: List<SongWithDetails>,
    ctx: MyAppContext,
    padding: PaddingValues,
    onSongClick: (idx: Int, song: SongWithDetails) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(), contentPadding = PaddingValues(8.dp)
    ) {
        itemsIndexed(songs) { idx, song ->
            SongItem(song = song, onItemClick = { onSongClick(idx, song) })
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
fun CurrentSongDisplay(song: SongWithDetails, ctx: MyAppContext) {

    val sliderPosition = remember { mutableFloatStateOf(0f) }
    val isPlaying = remember { mutableStateOf(ctx.player.isPlaying) }
    if (ctx.currentSong.value == null) {
        return
    }

    LaunchedEffect(ctx.player) {
        while (true) {
            // Update slider position based on current playback position
            if (song.duration > 0) {
                sliderPosition.floatValue =
                    ctx.player.currentPosition.toFloat() / song.duration.toFloat()
            }
            isPlaying.value = ctx.player.isPlaying
            delay(1000) // Update every second
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Slider(
                value = sliderPosition.floatValue,
                onValueChange = { sliderPosition.floatValue = it },
                onValueChangeFinished = {
                    ctx.player.seekTo(
                        (song.duration * sliderPosition.floatValue).toLong()
                    )
                },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        IconButton(
            onClick =
                {
                    ctx.playPrev()
                }) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous"
            )
        }
        IconButton(onClick = {
            if (ctx.player.isPlaying) {
                ctx.player.pause()
            } else {
                ctx.player.play()
            }
            isPlaying.value = !isPlaying.value
        }) {
            Icon(
                imageVector = if (isPlaying.value) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying.value) "Pause" else "Play"
            )
        }
        IconButton(
            onClick =
                {
                    ctx.playNext()
                }) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next"
            )
        }

    }
}
