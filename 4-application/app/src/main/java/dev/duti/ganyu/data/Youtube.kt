package dev.duti.ganyu.data

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
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
    val quality: String, val url: String, val width: Int, val height: Int
)

@Serializable
data class YoutubeFeedResp(
    val videos: List<ShortVideo>,
    val notifications: List<ShortVideo>
)


interface YoutubeApiService {
    @GET("api/v1/search")
    suspend fun searchVideos(@Query("q") query: String): List<ShortVideo>

    @FormUrlEncoded
    @POST("login")
    @Headers("Connection: close") // Prevent keep-alive
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("action") action: String = "signin"
    ): Response<ResponseBody>

    @GET("api/v1/auth/feed")
    suspend fun getSubscriptions(
        @Header("Cookie") authCookie: String,
        @Query("max_results") maxResults: Int = 10,
        @Query("page") page: Int = 1
    ): YoutubeFeedResp
}

object RetrofitClient {
    private const val BASE_URL = "https://iv.duti.dev/"
    private val okHttpClient by lazy {
        OkHttpClient.Builder().followRedirects(false) // Don't follow redirects automatically
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}

object YoutubeApiClient {
    private val apiService: YoutubeApiService by lazy {
        RetrofitClient.retrofit.create(YoutubeApiService::class.java)
    }
    private var cookies: String? = null

    private fun filterLenNotZero(vid: ShortVideo): Boolean {
        return vid.lengthSeconds != 0
    }

    suspend fun searchVideos(q: String): List<ShortVideo> {
        return apiService.searchVideos(q).filter() { vid -> filterLenNotZero(vid) }
    }

    suspend fun getSubscriptions(maxResults: Int = 20, page: Int = 1): List<ShortVideo> {
        if (cookies == null) {
            throw Exception("Invidious not authenticated. Remember to login")
        }
        val resp = apiService.getSubscriptions(
            cookies!!,
            maxResults,
            page
        )
        return resp.notifications.filter { filterLenNotZero(it) } + resp.videos.filter {
            filterLenNotZero(
                it
            )
        }
    }

    fun setCookies(cookie: String) {
        cookies = cookie
    }

    suspend fun loginAndGetCookies(email: String, password: String): String? {
        val resp = apiService.login(email, password)
        if (resp.code() == 302) {
            val cookie = resp.headers().values("set-cookie").first()
            setCookies(cookie)
            return cookie
        }
        return null
    }
}
