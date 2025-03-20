package dev.duti.ganyu.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.data.ShortVideo
import dev.duti.ganyu.data.YoutubeApiClient
import dev.duti.ganyu.ui.components.MusicSearchResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun YoutubeSubscriptions(ctx: MyAppContext, modifier: Modifier) {
    var subVideos by remember { mutableStateOf(emptyList<ShortVideo>()) }
    var currentPage by remember { mutableIntStateOf(1) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        if (ctx.ivIsLoggedIn.value) {

            LaunchedEffect(ctx.ivIsLoggedIn.value, currentPage) {
                scope.launch {
                    val newVideos = YoutubeApiClient.getSubscriptions(page = currentPage)
                    subVideos = if (currentPage == 1) newVideos else subVideos + newVideos
                }
            }
            // Show subscriptions
            MusicSearchResults(ctx, subVideos, { vid ->
                scope.launch(Dispatchers.IO) {
                    ctx.download(vid)
                }
            }, modifier = Modifier.weight(1f))
            // Pagination
            Button(onClick = { currentPage++ }) {
                Text("Load More")
            }
        } else {
            // Request login
            LoginScreen { cookie ->
                scope.launch {
                    ctx.ivLogin(cookie)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (cookie: String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Focus requesters for text fields
    val usernameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    // Auto-focus username field on launch
    LaunchedEffect(Unit) {
        usernameFocusRequester.requestFocus()
    }

    fun handleLogin() {
        focusManager.clearFocus()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val cookie = YoutubeApiClient.loginAndGetCookies(username, password)
                if (cookie == null) {
                    loginError = "Invalid credentials"
                    return@launch
                }
                onLoginSuccess(cookie)
            } catch (e: Exception) {
                loginError = "An unknown error occurred"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(), // Handle keyboard overlap
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
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(usernameFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            )
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { handleLogin() }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { handleLogin() }) {
            Text("Submit")
        }
    }
}
