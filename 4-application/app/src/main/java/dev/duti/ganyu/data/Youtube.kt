package dev.duti.ganyu.data

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@Serializable
data class ShortVideo(
    val type: String,
    val title: String,
    val videoId: String,
    val videoThumbnails: List<VideoThumbnail>,
    val lengthSeconds: Int,
    val author: String,
    val authorId: String,
    val authorUrl: String,
    val published: Long,
    val publishedText: String,
    val viewCount: Int
)

@Serializable
data class VideoThumbnail(
    val quality: String,
    val url: String,
    val width: Int,
    val height: Int
)


interface YoutubeApiService {
    @GET("api/v1/search")
    suspend fun searchVideos(@Query("q") query: String): List<ShortVideo>
}