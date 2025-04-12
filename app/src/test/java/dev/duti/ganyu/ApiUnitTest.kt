package dev.duti.ganyu

import dev.duti.ganyu.data.ShortVideo
import dev.duti.ganyu.data.YoutubeApiClient
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ApiUnitTest {
    @Test
    fun youtube_parsing() {
        val jsonString = """
        {
            "type": "shortVideo",
            "title": "How to Choose The Right Linux Distro in 2025",
            "videoId": "W2GwHEzG8Fg",
            "videoThumbnails": [
                {
                    "quality": "maxres",
                    "url": "https://iv.duti.dev/vi/W2GwHEzG8Fg/maxres.jpg",
                    "width": 1280,
                    "height": 720
                },
                {
                    "quality": "maxresdefault",
                    "url": "https://iv.duti.dev/vi/W2GwHEzG8Fg/maxresdefault.jpg",
                    "width": 1280,
                    "height": 720
                }
            ],
            "lengthSeconds": 811,
            "author": "The Linux Cast",
            "authorId": "UCylGUf9BvQooEFjgdNudoQg",
            "authorUrl": "/channel/UCylGUf9BvQooEFjgdNudoQg",
            "published": 1742003018,
            "publishedText": "3 hours ago",
            "viewCount": 646
        }
    """
        val shortVideo = Json.decodeFromString<ShortVideo>(jsonString)
        assertEquals(811, shortVideo.lengthSeconds)
        assertEquals("maxres", shortVideo.videoThumbnails[0].quality)
    }

    @Test
    fun search_api() = runTest {
        val videos = YoutubeApiClient.searchVideos("9Lana Propose")

        // Assertions
        assertNotNull(videos)
        assertEquals("XPLkkdMFmco", videos[0].videoId)
    }

    @Test
    fun login() = runTest {
        val successfulLogin = YoutubeApiClient.loginAndGetCookies("music", "music")
        assertNotNull(successfulLogin)
        val failedLogin = YoutubeApiClient.loginAndGetCookies("blah", "blah")
        assertEquals(null, failedLogin)
    }
}