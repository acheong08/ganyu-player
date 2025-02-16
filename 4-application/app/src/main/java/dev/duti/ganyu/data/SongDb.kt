package dev.duti.ganyu.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["path"], unique = true)]
)
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val title: String,
    val artistId: Long,
    val albumId: Long?,
    val duration: Int
)

@Entity(
    indices = [Index(value = ["name"], unique = true)]
)
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val art: String?,
    val artist: Long,
    val year: Int? = null
)

@Entity(
    indices = [Index(value = ["name"], unique = true)]
)
data class Artist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val art: String?
    // TODO: Metadata on artist such as youtube channel
)