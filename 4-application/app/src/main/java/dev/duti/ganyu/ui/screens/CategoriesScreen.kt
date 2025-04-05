package dev.duti.ganyu.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.ui.components.CategoryList
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun ArtistsScreen(ctx: MyAppContext, artists: List<Pair<String, Int>>, modifier: Modifier) {
    // Track the selected artist with remember
    var selectedCat by remember { mutableStateOf(false) }
    BackHandler(enabled = selectedCat) {
        // Handle back press when in artist detail view
        selectedCat = false
    }
    if (selectedCat) {

        // Show artist list if no artist is selected
        CategoryList(
            categories = artists.map { Triple(0L, it.first, it.second) }, onClick = { cat ->
                // Set the song filter and mark the artist as selected
                ctx.songFilterFunc.value = { song: SongWithDetails ->
                    song.artist == cat.second
                }
                selectedCat = true

            }, modifier = modifier
        )
    } else {
        // Show music player when an artist is selected
        MusicPlayerScreen(ctx, modifier = modifier)
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PlaylistsScreen(ctx: MyAppContext, modifier: Modifier) {
    var selectedCat by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    BackHandler(enabled = selectedCat) {
        selectedCat = false
    }

    if (selectedCat) {
        MusicPlayerScreen(ctx, modifier = modifier)
        return
    }

    val categories = ctx.repo.allPlaylists().collectAsState(listOf())

    Scaffold(modifier) { padding ->
        Column(
            modifier = Modifier.Companion.fillMaxSize()
        ) {
            CategoryList(
                categories = categories.value, onClick = { cat ->
                    scope.launch {
                        val songs = ctx.repo.getSongsInPlaylist(
                            cat.first
                        )
                        ctx.songFilterFunc.value = { song ->
                            songs.contains(song.id)
                        }
                        selectedCat = true
                    }
                }, modifier = Modifier
                    .padding(padding)
                    .weight(1f)  // This makes the list take all available space
                    .fillMaxWidth()
            )

            Button(
                onClick = { showDialog = true }, modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Create New Playlist")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Playlist") },
            text = {
                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Playlist name") })
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (playlistName.isNotBlank()) {
                            scope.launch {
                                ctx.repo.createPlaylist(playlistName)
                                playlistName = ""
                                showDialog = false
                            }
                        }
                    }) { Text("Create") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        playlistName = ""
                    }) { Text("Cancel") }
            })
    }
}