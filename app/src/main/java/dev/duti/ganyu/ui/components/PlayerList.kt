package dev.duti.ganyu.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(
    song: SongWithDetails,
    playlists: List<Triple<Long, String, Int>>,
    onItemClick: () -> Unit,
    onDelete: () -> Unit,
    onAddToPlaylist: (Long) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlaylistMenu by remember { mutableStateOf(false) }

    // Song item row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = { showMenu = true }
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art placeholder
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Album art",
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
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

    // Context menu (Delete/Add to Playlist)
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("Delete") },
            onClick = {
                showMenu = false
                showDeleteDialog = true
            }
        )
        DropdownMenuItem(
            text = { Text("Add to Playlist") },
            onClick = {
                showMenu = false
                showPlaylistMenu = true
            }
        )
    }

    // Playlist selection menu
    DropdownMenu(
        expanded = showPlaylistMenu,
        onDismissRequest = { showPlaylistMenu = false }
    ) {
        playlists.forEach { playlist ->
            DropdownMenuItem(
                text = { Text(playlist.second) }, // playlist.first = playlist name
                onClick = {
                    showPlaylistMenu = false
                    onAddToPlaylist(playlist.first)
                }
            )
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Song") },
            text = { Text("Are you sure you want to delete this song?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) { Text("Cancel") }
            }
        )
    }
}


@Composable
fun SongList(
    songs: List<SongWithDetails>,
    ctx: MyAppContext,
    padding: PaddingValues,
    onSongClick: (idx: Int, song: SongWithDetails) -> Unit
) {
    val playlists by ctx.repo.allPlaylists().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        itemsIndexed(songs) { idx, song ->
            SongItem(
                song = song,
                playlists = playlists,
                onItemClick = { onSongClick(idx, song) },
                onDelete = { ctx.deleteSong(song.path) },
                onAddToPlaylist = { playlistId ->
                    scope.launch {
                        ctx.repo.addSongToPlaylist(song.id, playlistId)
                    }
                }
            )
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
