package dev.duti.ganyu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.duti.ganyu.MyAppContext
import dev.duti.ganyu.data.ShortVideo
import dev.duti.ganyu.data.YoutubeApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

const val TAG = "YOUTUBE_COMPOSE"

@Composable
fun MusicSearchResults(
    ctx: MyAppContext,
    videos: List<ShortVideo>,
    onVideoClick: (ShortVideo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier, contentPadding = PaddingValues(8.dp)
    ) {
        items(videos.size) { videoIdx ->
            val video = videos[videoIdx]
            val thumbnail = video.videoThumbnails.find { it.quality == "medium" }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { onVideoClick(video) },
                border = if (ctx.songsMap.value.contains(video.videoId)) BorderStroke(
                    1.dp,
                    Color.Green
                ) else if (ctx.downloading.contains(video)) BorderStroke(
                    1.dp,
                    Color.Red
                ) else null
            ) {
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
fun YoutubeSearchScreen(ctx: MyAppContext, modifier: Modifier) {
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
            )
        )

        MusicSearchResults(
            ctx,
            videos = searchResults,
            onVideoClick = { vid ->
                scope.launch(Dispatchers.IO) {
                    ctx.download(vid)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}