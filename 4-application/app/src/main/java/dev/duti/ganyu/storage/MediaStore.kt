package dev.duti.ganyu.storage

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.duti.ganyu.data.Album
import dev.duti.ganyu.data.AlbumArtistCrossRef
import dev.duti.ganyu.data.Artist
import dev.duti.ganyu.data.Song
import dev.duti.ganyu.data.SongArtistCrossRef
import dev.duti.ganyu.data.SongWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first


@Dao
interface SongDao {
    @Insert
    suspend fun insert(song: Song): Long

    @Delete
    suspend fun delete(song: Song)

    @Query("SELECT * FROM song")
    fun getAllSongs(): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM song 
        WHERE title LIKE '%' || :searchTerm || '%'
    """
    )
    fun searchSongs(searchTerm: String): Flow<List<Song>>

    // We can use this with only 1 id to fetch for 1 artist
    @Query(
        """
        SELECT song.* 
        FROM song
        INNER JOIN song_artist ON song.id = song_artist.songId
        WHERE song_artist.artistId IN (:artistIds)
        GROUP BY song.id
    """
    )
    fun getSongsByAnyArtist(artistIds: List<Int>): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE albumId = :albumId")
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>
}

@Dao
interface AlbumDao {
    @Insert
    suspend fun insert(album: Album): Long

    @Delete
    suspend fun delete(album: Album)

    @Query("SELECT * FROM album")
    fun getAllAlbums(): Flow<List<Album>>

    // Search by title
    @Query("SELECT * FROM album WHERE title LIKE '%' || :searchTerm || '%'")
    fun searchAlbums(searchTerm: String): Flow<List<Album>>

    @Query("SELECT * FROM album WHERE title = :albumTitle LIMIT 1")
    fun getByAlbumTitle(albumTitle: String): Flow<Album?>

    // Get albums by artist(s) via junction table
    @Query(
        """
        SELECT album.* 
        FROM album
        INNER JOIN album_artist ON album.id = album_artist.albumId
        WHERE album_artist.artistId IN (:artistIds)
        GROUP BY album.id
    """
    )
    fun getAlbumsByArtists(artistIds: List<Int>): Flow<List<Album>>
}

@Dao
interface ArtistDao {
    @Insert
    suspend fun insert(artist: Artist): Long

    @Delete
    suspend fun delete(artist: Artist)

    @Query("SELECT * FROM artist")
    fun getAllArtists(): Flow<List<Artist>>

    @Query("SELECT * FROM artist WHERE name = :artistName LIMIT 1")
    fun getByArtistName(artistName: String): Flow<Artist?>

    @Query("SELECT * FROM artist WHERE name LIKE '%'|| :searchTerm ||'%'")
    fun searchArtists(searchTerm: String): Flow<List<Artist>>
}

@Dao
interface AlbumArtistDao {
    @Insert
    suspend fun insert(crossRef: AlbumArtistCrossRef): Long

    @Delete
    suspend fun delete(crossRef: AlbumArtistCrossRef)
}

@Dao
interface SongArtistDao {
    @Insert
    suspend fun insert(crossRef: SongArtistCrossRef): Long

    @Query("SELECT * FROM song_artist")
    fun getAllSongArtists(): Flow<List<SongArtistCrossRef>>

    @Query("DELETE FROM song_artist WHERE songId = :songId")
    suspend fun deleteBySongId(songId: Long)

    @Query("SELECT * FROM song_artist WHERE songId = :songId")
    suspend fun getSongArtists(songId: Long): List<SongArtistCrossRef>

    @Query("SELECT * FROM song_artist WHERE artistId = :artistId")
    suspend fun getArtistSongs(artistId: Long): List<SongArtistCrossRef>

}

class MusicRepository(
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val songArtistDao: SongArtistDao,
    private val albumArtistDao: AlbumArtistDao
) {
    // Song operations
    fun getAllSongs() = songDao.getAllSongs()
    suspend fun insertSong(song: SongWithDetails): Long {
        val albumId = if (song.album != null) {
            this.albumDao.getByAlbumTitle(song.album.title).first()?.id
                ?: this.albumDao.insert(song.album.toBasicAlbum())
        } else {
            null
        }
        val songId = this.songDao.insert(
            song.toBasicSong(albumId)
        )
        for (artist in song.artists) {
            val artistId =
                this.artistDao.getByArtistName(artist.name).first()?.id ?: this.artistDao.insert(
                    artist
                )
            this.songArtistDao.insert(SongArtistCrossRef(songId, artistId))

        }
        return songId
    }

    suspend fun deleteSong(song: Song) {
        this.songArtistDao.deleteBySongId(song.id)
        this.songDao.delete(song)
    }

    fun getAllSongArtists() = songArtistDao.getAllSongArtists()

    suspend fun getArtistSongs(artistId: Long) = songArtistDao.getArtistSongs(artistId)
    suspend fun getSongArtists(songId: Long) = songArtistDao.getSongArtists(songId)
}

@Database(
    entities = [
        Song::class,
        Album::class,
        Artist::class,
        SongArtistCrossRef::class,
        AlbumArtistCrossRef::class
    ],
    version = 1
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun songArtistDao(): SongArtistDao
    abstract fun albumArtistDao(): AlbumArtistDao

    companion object {
        @Volatile
        private var Instance: MusicDatabase? = null

        fun getDatabase(context: Context): MusicDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    MusicDatabase::class.java,
                    "music_database"
                ).build().also { Instance = it }
            }
        }
    }
}