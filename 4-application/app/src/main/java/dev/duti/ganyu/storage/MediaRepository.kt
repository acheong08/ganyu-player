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
import dev.duti.ganyu.data.AlbumWithDetails
import dev.duti.ganyu.data.Artist
import dev.duti.ganyu.data.Playlist
import dev.duti.ganyu.data.PlaylistSongCrossRef
import dev.duti.ganyu.data.Song
import dev.duti.ganyu.data.SongWithDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


@Dao
interface SongDao {
    @Insert
    suspend fun insert(song: Song): Long

    @Query("DELETE FROM song WHERE path = :path")
    suspend fun delete(path: Long)

    @Query("SELECT * FROM song")
    fun getAllSongs(): Flow<List<Song>>

    @Query(
        """
        SELECT * FROM song 
        WHERE title LIKE '%' || :searchTerm || '%'
    """
    )
    fun searchSongs(searchTerm: String): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE albumId = :albumId")
    fun getSongsByAlbum(albumId: Long): Flow<List<Song>>

    @Query("SELECT * FROM song WHERE path = :path LIMIT 1")
    fun getSongByPath(path: Long): Flow<Song?>
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
    @Query("SELECT * FROM album WHERE name LIKE '%' || :searchTerm || '%'")
    fun searchAlbums(searchTerm: String): Flow<List<Album>>

    @Query("SELECT * FROM album WHERE name = :albumTitle LIMIT 1")
    fun getByAlbumTitle(albumTitle: String): Flow<Album?>

    @Query("SELECT * FROM album WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Album?>
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

    @Query("SELECT * FROM artist WHERE id = :id LIMIT 1")
    fun getById(id: Long): Flow<Artist>

    @Query("SELECT * FROM artist WHERE name LIKE '%'|| :searchTerm ||'%'")
    fun searchArtists(searchTerm: String): Flow<List<Artist>>
}

@Dao
interface PlaylistDao {
    @Insert
    fun insert(playlist: Playlist)

    @Delete()
    fun delete(playlist: Playlist)
}

@Dao
interface PlaylistSongDao {
    @Insert
    fun insert(ps: PlaylistSongCrossRef)

    @Delete
    fun delete(ps: PlaylistSongCrossRef)

    @Query("SELECT * FROM ps_cross WHERE playlistId = :playlistId ")
    fun getByPlaylist(playlistId: Long): Flow<List<PlaylistSongCrossRef>>

    @Query("SELECT * FROM ps_cross WHERE songId = :songId ")
    fun getBySong(songId: Long): Flow<List<PlaylistSongCrossRef>>
}

class MusicRepository(
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
) {
    // Song operations
    fun getAllSongs() = songDao.getAllSongs()
    suspend fun insertSong(song: SongWithDetails): Long {
        val existingSong = songDao.getSongByPath(song.path).first()
        if ( existingSong != null) {
            return existingSong.id
        }
        val artistId =
            this.artistDao.getByArtistName(song.artist.name).first()?.id ?: this.artistDao.insert(
                song.artist
            )

        val albumId = if (song.album != null) {
            this.albumDao.getByAlbumTitle(song.album.name).first()?.id
                ?: this.albumDao.insert(song.album.toBasicAlbum(artistId))
        } else {
            null
        }
        val songId = this.songDao.insert(
            song.toBasicSong(artistId, albumId)
        )
        return songId
    }
    suspend fun deleteSong(song: Long) {
        songDao.delete(song)
    }
    fun getSongDetails(song: Song): Flow<SongWithDetails> {
        return flow {

            val artist = artistDao.getById(song.artistId).first()
            val album = if (song.albumId != null) albumDao.getById(song.albumId).first() else null
            emit(SongWithDetails(song.path, song.title,
                album?.let { AlbumWithDetails.fromBasicAlbum(it, artist) }, song.duration, artist))
        }
    }
    fun getAllArtists() = artistDao.getAllArtists()
    fun getAllAlbums() = albumDao.getAllAlbums()
}

@Database(
    entities = [
        Song::class,
        Album::class,
        Artist::class,
    ], version = 1
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao

    companion object {
        @Volatile
        private var Instance: MusicDatabase? = null

        fun getDatabase(context: Context): MusicDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, MusicDatabase::class.java, "music_database"
                ).build().also { Instance = it }
            }
        }
    }
}
