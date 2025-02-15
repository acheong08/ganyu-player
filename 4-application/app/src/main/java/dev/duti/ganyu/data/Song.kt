package dev.duti.ganyu.data

data class AlbumWithDetails (
    val title: String,
    val art: String?,
    val artists: List<Artist>,
    val year: Int? = null
) {
    fun toBasicAlbum(): Album {
        return Album(title = title, art = art, year = year)
    }
}

data class SongWithDetails (
    val path: String,
    val title: String,
    val album: AlbumWithDetails?,
    val duration: Int,
    val artists: List<Artist>,
    val id: Long = -1,
) {
    fun toBasicSong(albumId: Long?): Song {
        return Song(title = title, path = path, duration = duration, albumId = albumId)
    }
}