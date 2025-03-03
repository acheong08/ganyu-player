package dev.duti.ganyu.ui

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
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import dev.duti.ganyu.storage.MusicRepository
import dev.duti.ganyu.ui.songs.MusicPlayerScreen
import kotlinx.coroutines.launch

@UnstableApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(player: MediaController, repo: MusicRepository) {
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
            },
            drawerState = drawerState
    ) {
        // Screen content

        Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                            title = { Text(screen.toString()) },
                            navigationIcon = {
                                IconButton(
                                        onClick = {
                                            scope.launch {
                                                if (drawerState.isClosed) {
                                                    drawerState.open()
                                                } else {
                                                    drawerState.close()
                                                }
                                            }
                                        }
                                ) { Icon(Icons.Default.Menu, contentDescription = "Menu") }
                            }
                    )
                }
        ) { innerPadding ->
            when (screen) {
                Screens.SONGS -> {
                    MusicPlayerScreen(repo, player, modifier = Modifier.padding(innerPadding))
                }
                else -> Text(text = screen.toString(), modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
