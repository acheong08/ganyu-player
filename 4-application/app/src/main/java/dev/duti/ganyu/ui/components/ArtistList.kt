package dev.duti.ganyu.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ArtistList(
    artists: List<Pair<String, Int>>,
    onArtistClick: (String) -> Unit,  // Callback when artist is clicked
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(artists) { artist ->
                ArtistItem(
                    name = artist.first,
                    trackCount = artist.second,
                    onClick = { onArtistClick(artist.first) }  // Pass click to parent
                )
            }
        }
    }
}

@Composable
fun ArtistItem(
    name: String,
    trackCount: Int,
    onClick: () -> Unit,  // Click handler
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),  // Make whole card clickable
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name /* ... */)
            Text(text = "$trackCount tracks" /* ... */)
        }
    }
}

// Updated Preview
@Preview(showBackground = true)
@Composable
fun ArtistListPreview() {
    val sampleArtists = listOf(
        "The Beatles" to 213,
        "Pink Floyd" to 165,
        "Led Zeppelin" to 87
    )

    MaterialTheme {
        ArtistList(
            artists = sampleArtists,
            onArtistClick = { artistName ->
                println("Artist clicked: $artistName")
                // Handle click (e.g., navigate to detail screen)
            }
        )
    }
}