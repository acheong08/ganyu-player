package dev.duti.ganyu.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val title: String,
    val albumId: Long?,
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

@Entity(
    indices = [Index(value = ["title"], unique = true)]
)
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val art: String?,
    val year: Int? = null
)

@Entity(
    tableName = "album_artist",
    primaryKeys = ["albumId", "artistId"]
)
data class AlbumArtistCrossRef(
    val albumId: Long,
    val artistId: Long
)

@Entity(
    indices = [Index(value = ["name"], unique = true)]
)
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val art: String?
)