package dev.duti.ganyu.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.ui.components.ArtistList
import dev.duti.ganyu.ui.components.MusicSearchResults
import dev.duti.ganyu.ui.components.YoutubeSearchScreen
import dev.duti.ganyu.ui.screens.MusicPlayerScreen
import dev.duti.ganyu.ui.screens.YoutubeSubscriptions
import dev.duti.ganyu.utils.getArtists
import kotlinx.coroutines.launch

@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(ctx: MyAppContext) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var screen by remember { mutableStateOf(Screens.SONGS) }
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = {
            NavDrawer { chosenScreen ->
                scope.launch {
                    screen = chosenScreen
                    drawerState.close()
                }
            }
        }, drawerState = drawerState
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(), topBar = {
                TopAppBar(title = { Text(screen.toString()) }, navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                })
            }) { innerPadding ->
            val modifier = Modifier.padding(
                PaddingValues(
                    top = maxOf(
                        innerPadding.calculateTopPadding() - 7.dp,
                        0.dp
                    ), // Reduce top padding
                    bottom = innerPadding.calculateBottomPadding(),
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
            )
            when (screen) {
                Screens.SONGS -> {
                    ctx.songFilterFunc.value = { true }
                    MusicPlayerScreen(ctx, modifier = modifier)
                }

                Screens.ARTISTS -> {

                    // Track the selected artist with remember
                    var selectedArtist by remember { mutableStateOf<String?>(null) }
                    BackHandler(enabled = selectedArtist != null) {
                        // Handle back press when in artist detail view
                        selectedArtist = null
                    }
                    if (selectedArtist == null) {
                        // Show artist list if no artist is selected
                        ArtistList(
                            artists = getArtists(ctx.ctx),
                            onArtistClick = { artist ->
                                // Set the song filter and mark the artist as selected
                                ctx.songFilterFunc.value = { song: SongWithDetails ->
                                    song.artist == artist
                                }
                                selectedArtist = artist
                            },
                            modifier = modifier
                        )
                    } else {
                        // Show music player when an artist is selected
                        MusicPlayerScreen(ctx, modifier = modifier)
                    }
                }

                Screens.SEARCH -> {
                    YoutubeSearchScreen(ctx, modifier)
                }

                Screens.DOWNLOADING -> {
                    Column(modifier = modifier) {
                        MusicSearchResults(
                            ctx,
                            ctx.downloading,
                            {},
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Screens.SUBSCRIPTIONS -> {
                    YoutubeSubscriptions(ctx, modifier)
                }

            }
        }
    }
}
