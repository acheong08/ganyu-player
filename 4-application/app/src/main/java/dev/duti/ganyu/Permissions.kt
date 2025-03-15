package dev.duti.ganyu

import android.content.pm.PackageManager

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

interface PermissionRequestCallback {
    fun onAllPermissionsGranted()
    fun onPermissionsDenied(redirectToSettings: Boolean)
}

class PermissionRequester(private val activity: ComponentActivity) {
    private lateinit var callback: PermissionRequestCallback
    private var permissionsToRequest: List<String> = emptyList()

    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results -> handlePermissionResults(results) }

    fun requestPermissions(permissions: List<String>, callback: PermissionRequestCallback) {
        this.callback = callback
        this.permissionsToRequest = permissions

        val ungrantedPermissions =
            permissions.filter { permission ->
                ContextCompat.checkSelfPermission(activity, permission) !=
                        PackageManager.PERMISSION_GRANTED
            }

        if (ungrantedPermissions.isEmpty()) {
            callback.onAllPermissionsGranted()
            return
        }

        val shouldShowRationale =
            ungrantedPermissions.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }

        if (shouldShowRationale) {
            showRationaleDialog(ungrantedPermissions)
        } else {
            launchPermissionRequest(ungrantedPermissions)
        }
    }

    private fun showRationaleDialog(ungrantedPermissions: List<String>) {
        AlertDialog.Builder(activity)
            .setTitle("Permission Required")
            .setMessage(
                "This app needs the following permissions to function properly. Please grant them when prompted."
            )
            .setPositiveButton("Continue") { _, _ ->
                launchPermissionRequest(ungrantedPermissions)
            }
            .setNegativeButton("Cancel") { _, _ -> callback.onPermissionsDenied(false) }
            .setCancelable(false)
            .show()
    }

    private fun launchPermissionRequest(permissions: List<String>) {
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun handlePermissionResults(results: Map<String, Boolean>) {
        val allGranted = results.all { it.value }

        if (allGranted) {
            callback.onAllPermissionsGranted()
            return
        }

        val deniedPermissions = results.filter { !it.value }.keys
        val hasPermanentlyDenied =
            deniedPermissions.any { permission ->
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }

        callback.onPermissionsDenied(hasPermanentlyDenied)
    }
}