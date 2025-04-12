package dev.duti.ganyu.data


data class SongWithDetails(
    val path: Long,
    val title: String,
    val album: String?,
    val duration: Long,
    val artist: String,
    val id: String
)

