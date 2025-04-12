package dev.duti.ganyu.storage

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String
)

@Entity(
    tableName = "song_playlist_cross_ref",
    primaryKeys = ["songId", "playlistId"],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playlistId")]
)
data class SongPlaylistCrossRef(
    val songId: String,
    val playlistId: Long
)

data class PlaylistWithSongCount(
    val id: Long,
    val name: String,
    val songCount: Int
)

@Dao
interface PlaylistDao {
    // Playlist operations
    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)


    @Query(
        """
    SELECT p.id, p.name, COUNT(ref.songId) as songCount
    FROM playlists p 
    LEFT JOIN song_playlist_cross_ref ref ON p.id = ref.playlistId
    GROUP BY p.id
    ORDER BY p.name ASC
"""
    )
    fun getAllPlaylistsWithSongCount(): Flow<List<PlaylistWithSongCount>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): Playlist?

    // Song-Playlist operations
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: SongPlaylistCrossRef)

    @Delete
    suspend fun removeSongFromPlaylist(crossRef: SongPlaylistCrossRef)

    @Query("DELETE FROM song_playlist_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylistById(playlistId: Long, songId: String)

    @Query("SELECT songId FROM song_playlist_cross_ref WHERE playlistId = :playlistId")
    suspend fun getSongsInPlaylist(playlistId: Long): List<String>


    @Query("SELECT id FROM playlists WHERE name = :name LIMIT 1")
    suspend fun getPlaylistIdByName(name: String): Long?
}

@Database(
    entities = [Playlist::class, SongPlaylistCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile
        private var INSTANCE: PlaylistDatabase? = null

        fun getDatabase(context: Context): PlaylistDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlaylistDatabase::class.java,
                    "playlist_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    fun allPlaylists(): Flow<List<Triple<Long, String, Int>>> {
        return playlistDao.getAllPlaylistsWithSongCount()
            .map { it.map { Triple(it.id, it.name, it.songCount) } }
    }

    suspend fun getPlaylistIdByName(name: String) = playlistDao.getPlaylistIdByName(name)

    // Playlist operations
    suspend fun createPlaylist(name: String): Long {
        val playlist = Playlist(name = name)
        return playlistDao.insertPlaylist(playlist)
    }

    suspend fun renamePlaylist(playlistId: Long, newName: String) {
        val playlist = playlistDao.getPlaylistById(playlistId)
        playlist?.let {
            it.name = newName
            playlistDao.updatePlaylist(it)
        }
    }

    suspend fun deletePlaylist(playlistId: Long) {
        val playlist = playlistDao.getPlaylistById(playlistId)
        playlist?.let {
            playlistDao.deletePlaylist(it)
        }
    }

    // Song-Playlist operations
    suspend fun addSongToPlaylist(songId: String, playlistId: Long) {
        val crossRef = SongPlaylistCrossRef(songId, playlistId)
        playlistDao.addSongToPlaylist(crossRef)
    }

    suspend fun removeSongFromPlaylist(songId: String, playlistId: Long) {
        playlistDao.removeSongFromPlaylistById(playlistId, songId)
    }

    suspend fun getSongsInPlaylist(playlistId: Long): List<String> {
        return playlistDao.getSongsInPlaylist(playlistId)
    }

}