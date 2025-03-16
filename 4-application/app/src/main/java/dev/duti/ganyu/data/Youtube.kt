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

    suspend fun searchVideos(q: String) = apiService.searchVideos(q)

    suspend fun loginAndGetCookies(email: String, password: String): String? {
        val resp = apiService.login(email, password)
        if (resp.code() == 302) {
            return resp.headers().values("set-cookie").first()
        }
        return null
    }
}
