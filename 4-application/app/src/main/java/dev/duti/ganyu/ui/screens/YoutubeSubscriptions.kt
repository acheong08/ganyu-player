package dev.duti.ganyu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.data.ShortVideo
import dev.duti.ganyu.data.YoutubeApiClient
import dev.duti.ganyu.ui.components.MusicSearchResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun YoutubeSubscriptions(ctx: MyAppContext, modifier: Modifier) {
    var subVideos by remember { mutableStateOf(emptyList<ShortVideo>()) }
    var currentPage by remember { mutableIntStateOf(1) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        if (ctx.ivLoggedIn.value) {

            LaunchedEffect(ctx.ivLoggedIn.value, currentPage) {
                scope.launch {
                    val newVideos = YoutubeApiClient.getSubscriptions(page = currentPage)
                    subVideos = if (currentPage == 1) newVideos else subVideos + newVideos
                }
            }
            // Show subscriptions
            Box(modifier = Modifier.weight(1f)) {
                MusicSearchResults(subVideos, { vid ->
                    scope.launch(Dispatchers.IO) {
                        ctx.download(vid)
                    }
                }, modifier = Modifier.fillMaxSize())
            }

            // Pagination
            Button(onClick = { currentPage++ }) {
                Text("Load More")
            }
        } else {
            // Request login
            LoginScreen {
                ctx.ivLoggedIn.value = true
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (loginError.isNotEmpty()) {
            Text(text = loginError, color = MaterialTheme.colorScheme.error)
        }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            focusManager.clearFocus()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    YoutubeApiClient.loginAndGetCookies(username, password)
                    withContext(Dispatchers.Main) {
                        onLoginSuccess()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        loginError = "Invalid credentials"
                    }
                }
            }
        }) {
            Text("Submit")
        }
    }
}