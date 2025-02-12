package dev.duti.ganyu.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val title: String,
    val albumId: Int,
    val duration: Int
)

@Entity(
    tableName = "song_artist",
    primaryKeys = ["songId", "artistId"]
)
data class SongArtistCrossRef(
    val songId: Int,
    val artistId: Int
)

@Entity
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val links: List<ArtistLink>,
    val art: Uri?
)

@Entity(
    tableName = "album_artist",
    primaryKeys = ["albumId", "artistId"]
)
data class AlbumArtistCrossRef(
    val albumId: Int,
    val artistId: Int
)

data class ArtistLink(
    val platform: String,
    val url: String
)
@Entity
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val links: List<ArtistLink>,
    val art: Uri?
)