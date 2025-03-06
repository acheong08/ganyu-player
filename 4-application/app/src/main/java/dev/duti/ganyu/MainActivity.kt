package dev.duti.ganyu

import android.Manifest
import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.duti.ganyu.storage.MusicDatabase
import dev.duti.ganyu.storage.MusicRepository
import dev.duti.ganyu.storage.getLocalMediaFiles
import dev.duti.ganyu.ui.MainView
import dev.duti.ganyu.ui.theme.GanyuTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), PermissionRequestCallback {
    private lateinit var mediaController: MediaController
    private lateinit var controllerFuture: ListenableFuture<MediaController>


    private lateinit var db: MusicDatabase
    private lateinit var repo: MusicRepository

    private val scope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize database
        db = MusicDatabase.getDatabase(applicationContext)
        repo = MusicRepository(db.songDao(), db.albumDao(), db.artistDao())
        // Media player service
        controllerFuture = MediaController.Builder(
            applicationContext,
            SessionToken(
                applicationContext,
                ComponentName(applicationContext, PlaybackService::class.java)
            )
        ).buildAsync()
        controllerFuture.addListener(
            { mediaController = controllerFuture.get() }, MoreExecutors.directExecutor()
        )
        // Start Python
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(applicationContext))
        }
        val py = Python.getInstance()
        val module = py.getModule("main")
        val result = module.callAttr("main").toJava(String::class.java)
        Log.i("PYTHON", result)

        // Look for song files in background
        scope.launch {
            for (song in getLocalMediaFiles(applicationContext)) {
                repo.insertSong(song)
            }
        }

        val permissionRequester = PermissionRequester(this)
        permissionRequester.requestPermissions(listOf(Manifest.permission.READ_MEDIA_AUDIO), this)
    }

    @UnstableApi
    override fun onAllPermissionsGranted() {
        // Check if controller is ready
        if (controllerFuture.isDone) {
            mediaController = controllerFuture.get()
            setContent { GanyuTheme { MainView(mediaController, repo) } }
        } else {
            // Wait for controller to be ready
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                setContent { GanyuTheme { MainView(mediaController, repo) } }
            }, MoreExecutors.directExecutor())
        }
    }

    override fun onPermissionsDenied(redirectToSettings: Boolean) {
        setContent { GanyuTheme { Text("Yeah no you kinda need media perms bro") } }
    }

    override fun onDestroy() {
        MediaController.releaseFuture(controllerFuture)
        super.onDestroy()
        db.close()
    }
}
