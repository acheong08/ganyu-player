package dev.duti.ganyu

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.duti.ganyu.data.AlbumWithDetails
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
        )
    }

    @Test
    fun insertSong() = runBlocking {
        val artist = Artist(
            name = "9Lana",
            art = null
        )
        val song = SongWithDetails(
            path = "example",
            title = "Propose",
            album = AlbumWithDetails("Unknown Album", null, artist),
            duration = 120,
            artist = artist

        )
        val songId = repo.insertSong(song)
        assert(repo.getAllSongs().first()[0].id == songId)
        assert(repo.getAllArtists().first()[0].name == "9Lana")
        assert(repo.getAllAlbums().first()[0].name == song.album?.name)
    }

    @After
    fun teardown() {
        db.close()
    }
}