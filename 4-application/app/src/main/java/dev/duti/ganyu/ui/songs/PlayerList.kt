package dev.duti.ganyu.ui.songs

import android.content.ContentUris
import android.provider.MediaStore
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dev.duti.ganyu.R
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.storage.MusicRepository


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
            painter = painterResource(R.drawable.ic_launcher_foreground), // Replace with actual artwork
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
                text = song.artist.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CurrentSongDisplay(song: SongWithDetails, isPlaying: Boolean, onPlayPauseClick: () -> Unit) {
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
                text = song.artist.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = {
            onPlayPauseClick()
        }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }
    }
}

@Composable
fun MusicPlayerScreen(repo: MusicRepository, player: ExoPlayer, modifier: Modifier) {
    var currentSong by remember { mutableStateOf<SongWithDetails?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    val songs = repo.getAllSongs().collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = {
            currentSong?.let { song ->
                CurrentSongDisplay(song = song, isPlaying, onPlayPauseClick = {
                    if (player.isPlaying) {
                        player.pause()
                    } else {
                        player.play()
                    }
                    isPlaying = !isPlaying

                })
            }
        }, modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(), contentPadding = PaddingValues(8.dp)
        ) {
            itemsIndexed(songs.value) { _, song ->
                val fullSong = repo.getSongDetails(song).collectAsState(SongWithDetails.empty())
                SongItem(song = fullSong.value, onItemClick = {
                    currentSong = fullSong.value
                    player.setMediaItem(
                        MediaItem.fromUri(
                            ContentUris.withAppendedId(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.path
                            )
                        )
                    )
                    player.prepare()
                })
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

        }
    }
}