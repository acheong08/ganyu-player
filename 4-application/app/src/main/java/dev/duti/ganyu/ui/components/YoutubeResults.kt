package dev.duti.ganyu.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import dev.duti.ganyu.data.ShortVideo
import dev.duti.ganyu.data.YoutubeApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MusicSearchResults(
    videos: List<ShortVideo>, onVideoClick: (String) -> Unit, modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier, contentPadding = PaddingValues(8.dp)
    ) {
        items(videos.size) { videoIdx ->
            val video = videos[videoIdx]
            val thumbnail = video.videoThumbnails.find { it.quality == "medium" }

            Log.i("YT_SEARCH", thumbnail.toString())

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { onVideoClick(video.videoId) }) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    thumbnail?.let {
                        AsyncImage(
                            model = it.url,
                            contentDescription = null,
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = video.title, fontWeight = FontWeight.Bold, fontSize = 16.sp
                        )
                        Text(
                            text = video.author, fontSize = 14.sp
                        )
                        Text(
                            text = formatDuration(video.lengthSeconds), fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format(Locale.SIMPLIFIED_CHINESE, "%d:%02d", minutes, remainingSeconds)
}


@Composable
fun YoutubeSearchScreen(modifier: Modifier) {
    var query by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<ShortVideo>()) }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        TextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .focusRequester(focusRequester),
            placeholder = { Text("Search music...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    scope.launch(Dispatchers.IO) {
                        searchResults = YoutubeApiClient.searchVideos(query)
                    }
                    focusManager.clearFocus()
                }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ))

        MusicSearchResults(
            videos = searchResults,
            onVideoClick = { /* Empty for now */ },
            modifier = Modifier.fillMaxSize()
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}