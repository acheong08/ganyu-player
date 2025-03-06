package dev.duti.ganyu

import android.content.Context
import androidx.media3.session.MediaController
import dev.duti.ganyu.storage.MusicRepository
import dev.duti.ganyu.storage.getLocalMediaFiles

data class MyAppContext(
    val ctx: Context,
    val repo: MusicRepository,
    val player: MediaController,
) {
    suspend fun reloadMusicDb() {
        for (song in getLocalMediaFiles(ctx)) {
            repo.insertSong(song)
        }
    }
}