package dev.duti.ganyu.data

data class AlbumWithDetails (
    val name: String,
    val art: String?,
    val artist: Artist,
    val year: Int? = null
) {
    fun toBasicAlbum(artistId: Long): Album {
        return Album(name = name, art = art, artist = artistId , year = year)
    }
}

data class SongWithDetails (
    val path: Long,
    val title: String,
    val album: AlbumWithDetails?,
    val duration: Int,
    val artist: Artist,
    val id: Long = -1,
) {
    fun toBasicSong(artistId: Long, albumId: Long?): Song {
        return Song(title = title, path = path, duration = duration, artistId = artistId, albumId = albumId)
    }
}