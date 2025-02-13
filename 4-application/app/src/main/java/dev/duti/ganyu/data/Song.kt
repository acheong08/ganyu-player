package dev.duti.ganyu.data

data class SongWithDetails (
    val path: String,
    val title: String,
    val albumId: Int,
    val duration: Int,
    val artists: List<Artist>,
)