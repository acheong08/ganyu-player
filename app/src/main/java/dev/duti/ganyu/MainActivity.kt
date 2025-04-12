package dev.duti.ganyu

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dev.duti.ganyu.storage.SettingsRepository
import dev.duti.ganyu.ui.MainView
import dev.duti.ganyu.ui.theme.GanyuTheme

class MainActivity : ComponentActivity(), PermissionRequestCallback {
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var mediaController: MediaController
    private lateinit var myAppCtx: MyAppContext
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    // private lateinit var repo: MusicRepository

    // private lateinit var db: MusicDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize database
        // db = MusicDatabase.getDatabase(applicationContext)
        // repo = MusicRepository(db.songDao(), db.albumDao(), db.artistDao())
        // Media player service
        controllerFuture = MediaController.Builder(
            applicationContext, SessionToken(
                applicationContext, ComponentName(applicationContext, PlaybackService::class.java)
            )
        ).buildAsync()
        controllerFuture.addListener(
            { mediaController = controllerFuture.get() }, MoreExecutors.directExecutor()
        )
        val permissionRequester = PermissionRequester(this)
        permissionRequester.requestPermissions(listOf(Manifest.permission.READ_MEDIA_AUDIO), this)
    }

    @UnstableApi
    fun completeStart() {
        myAppCtx = MyAppContext(applicationContext, mediaController, SettingsRepository(dataStore))
        setContent { GanyuTheme { MainView(myAppCtx) } }
    }

    @UnstableApi
    override fun onAllPermissionsGranted() {
        // Check if controller is ready
        if (controllerFuture.isDone) {
            mediaController = controllerFuture.get()
            completeStart()
        } else {
            // Wait for controller to be ready
            controllerFuture.addListener({
                mediaController = controllerFuture.get()
                completeStart()
            }, MoreExecutors.directExecutor())
        }
    }

    override fun onPermissionsDenied(redirectToSettings: Boolean) {
        setContent { GanyuTheme { Text("Yeah no you kinda need media perms bro") } }
    }

    override fun onDestroy() {
        MediaController.releaseFuture(controllerFuture)
        myAppCtx.destroy()
        super.onDestroy()
    }
}
