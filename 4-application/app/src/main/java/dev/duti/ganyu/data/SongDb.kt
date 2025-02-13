package dev.duti.ganyu.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val title: String,
    val albumId: Long,
    val duration: Int
)

@Entity(
    tableName = "song_artist",
    primaryKeys = ["songId", "artistId"]
)
data class SongArtistCrossRef(
    val songId: Long,
    val artistId: Long
)

@Entity
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val art: String?
)

@Entity(
    tableName = "album_artist",
    primaryKeys = ["albumId", "artistId"]
)
data class AlbumArtistCrossRef(
    val albumId: Long,
    val artistId: Long
)

@Entity
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val art: String?
)