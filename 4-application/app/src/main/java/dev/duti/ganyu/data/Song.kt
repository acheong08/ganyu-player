package dev.duti.ganyu.data

data class AlbumWithDetails(
    val name: String,
    val art: String?,
    val artist: Artist,
    val year: Int? = null
) {
    fun toBasicAlbum(artistId: Long): Album {
        return Album(name = name, art = art, artist = artistId, year = year)
    }

    companion object {
        fun fromBasicAlbum(album: Album, artist: Artist): AlbumWithDetails {
            return AlbumWithDetails(album.name, album.art, artist, album.year)
        }
    }
}

data class SongWithDetails(
    val path: Long,
    val title: String,
    val album: AlbumWithDetails?,
    val duration: Long,
    val artist: Artist,
) {
    fun toBasicSong(artistId: Long, albumId: Long?): Song {
        return Song(
            title = title, path = path, duration = duration, artistId = artistId, albumId = albumId
        )
    }

    companion object {
        fun empty(): SongWithDetails {
            return SongWithDetails(0, "", null, 0, Artist(0, "", null))
        }
    }
}

