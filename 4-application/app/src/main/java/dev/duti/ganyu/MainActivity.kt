package dev.duti.ganyu

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import dev.duti.ganyu.ui.MainView
import dev.duti.ganyu.ui.theme.GanyuTheme
import android.Manifest
import android.os.Build

class MainActivity : ComponentActivity(), PermissionRequestCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val permissionRequester = PermissionRequester(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionRequester.requestPermissions(listOf(Manifest.permission.READ_MEDIA_AUDIO), this)
        } else {
            onAllPermissionsGranted()
        }
    }
    override fun onAllPermissionsGranted() {
        setContent {
            GanyuTheme {
                MainView(this.applicationContext)
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
}
