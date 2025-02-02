package dev.duti.ganyu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.duti.ganyu.device.Contact
import dev.duti.ganyu.device.Device
import dev.duti.ganyu.ui.theme.GanyuTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity(), PermissionRequestCallback {
    private lateinit var permissionRequester: PermissionRequester
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permissionRequester = PermissionRequester(this)
        permissionRequester.requestPermissions(
                listOf(android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.POST_NOTIFICATIONS),
                this
        )
        startForegroundService(Intent(this, MainService::class.java))
    }
    override fun onAllPermissionsGranted() {
        val device = Device().apply { init(this@MainActivity) }
        setContent {
            GanyuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactsList(device = device, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onPermissionsDenied(redirectToSettings: Boolean) {
        setContent {
            GanyuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text("Permission denied", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ContactsList(device: Device, modifier: Modifier = Modifier) {
    var contacts = device.getContacts().contactsList

    LaunchedEffect(Unit) {
        contacts = withContext(Dispatchers.IO) { device.getContacts().contactsList }
    }

    if (contacts.isEmpty()) {
        Text("No contacts found", modifier = modifier)
    } else {
        LazyColumn(modifier = modifier) {
            items(contacts.size) { contact -> ContactItem(contact = contacts[contact]) }
        }
    }
}

@Composable
fun ContactItem(contact: Contact) {
    Text(text = "${contact.name}: ${contact.phoneNumber}", modifier = Modifier.padding(16.dp))
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GanyuTheme { Greeting("Android") }
}
