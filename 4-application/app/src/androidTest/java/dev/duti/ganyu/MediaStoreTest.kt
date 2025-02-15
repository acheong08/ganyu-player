package dev.duti.ganyu

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.duti.ganyu.data.Artist
import dev.duti.ganyu.data.SongWithDetails
import dev.duti.ganyu.storage.MusicDatabase
import dev.duti.ganyu.storage.MusicRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaStoreTest {
    private lateinit var db: MusicDatabase
    private lateinit var repo: MusicRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MusicDatabase::class.java)
            .allowMainThreadQueries().build()
        repo = MusicRepository(
            db.songDao(),
            db.albumDao(),
            db.artistDao(),
            db.songArtistDao(),
            db.albumArtistDao()
        )
    }

    @Test
    fun insertSong() = runBlocking {
        val song = SongWithDetails(
            path = "example",
            title = "Propose",
            album = null,
            duration = 120,
            artists = listOf(
                Artist(
                    name = "9Lana",
                    art = null
                )
            )
        )
        val songId = repo.insertSong(song)
        assert(repo.getAllSongs().first()[0].id == songId)
        assert(repo.getAllSongArtists().first()[0].songId == songId)
        assert(repo.getSongArtists(songId)[0].songId == songId)
    }

    @After
    fun teardown() {
        db.close()
    }
}