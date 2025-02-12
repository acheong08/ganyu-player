package dev.duti.ganyu.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.duti.ganyu.data.Album
import dev.duti.ganyu.data.Artist
import dev.duti.ganyu.data.Song
import kotlinx.coroutines.flow.Flow


@Dao
interface SongDao {
    @Insert
    suspend fun insert(song: Song)

    @Delete
    suspend fun delete(song: Song)

    @Query("SELECT * FROM song")
    fun getAllSongs(): Flow<List<Song>>

    @Query("""
        SELECT * FROM song 
        WHERE title LIKE '%' || :searchTerm || '%'
    """)
    fun searchSongs(searchTerm: String): Flow<List<Song>>

    // We can use this with only 1 id to fetch for 1 artist
    @Query("""
        SELECT song.* 
        FROM song
        INNER JOIN song_artist ON song.id = song_artist.songId
        WHERE song_artist.artistId IN (:artistIds)
        GROUP BY song.id
    """)
    fun getSongsByAnyArtist(artistIds: List<Int>): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE albumId = :albumId")
    fun  getSongsByAlbum(albumId: Int)
}

@Dao
interface AlbumDao {
    @Insert
    suspend fun insert(album: Album)

    @Delete
    suspend fun delete(album: Album)

    @Query("SELECT * FROM album")
    fun getAllAlbums(): Flow<List<Album>>

    // Search by title
    @Query("SELECT * FROM album WHERE title LIKE '%' || :searchTerm || '%'")
    fun searchAlbums(searchTerm: String): Flow<List<Album>>

    // Get albums by artist(s) via junction table
    @Query("""
        SELECT album.* 
        FROM album
        INNER JOIN album_artist ON album.id = album_artist.albumId
        WHERE album_artist.artistId IN (:artistIds)
        GROUP BY album.id
    """)
    fun getAlbumsByArtists(artistIds: List<Int>): Flow<List<Album>>
}

@Dao
interface ArtistDao {
    @Insert
    suspend fun insert(artist: Artist)

    @Delete
    suspend fun delete(artist: Artist)

    @Query("SELECT * FROM artist")
    fun getAllArtists(): Flow<List<Artist>>

    @Query("SELECT * FROM artist WHERE name LIKE '%'|| :searchTerm ||'%'")
    fun searchArtists(searchTerm: String)
}

