package dev.duti.ganyu

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.media3.exoplayer.ExoPlayer
import dev.duti.ganyu.storage.MusicDatabase
import dev.duti.ganyu.storage.MusicRepository
import dev.duti.ganyu.storage.getLocalMediaFiles
import dev.duti.ganyu.ui.MainView
import dev.duti.ganyu.ui.theme.GanyuTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), PermissionRequestCallback {
    private lateinit var player: ExoPlayer
    private lateinit var db: MusicDatabase
    private lateinit var repo: MusicRepository
    private val scope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = MusicDatabase.getDatabase(applicationContext)
        repo = MusicRepository(db.songDao(), db.albumDao(), db.artistDao())
        scope.launch {
            for (song in getLocalMediaFiles(applicationContext)) {
                repo.insertSong(song)
            }
        }

        val permissionRequester = PermissionRequester(this)
        player = ExoPlayer.Builder(applicationContext).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequester.requestPermissions(
                listOf(Manifest.permission.READ_MEDIA_AUDIO),
                this
            )
        } else {
            onAllPermissionsGranted()
        }
    }

    override fun onAllPermissionsGranted() {
        setContent {
            GanyuTheme {
                MainView(player, repo)
            }
        }
    }

    override fun onPermissionsDenied(redirectToSettings: Boolean) {
        setContent {
            GanyuTheme {
                Text("Yeah no you kinda need media perms bro")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
