package dev.duti.ganyu

import dev.duti.ganyu.data.YoutubeApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://iv.duti.dev/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object YoutubeApiClient {
    val apiService: YoutubeApiService by lazy {
        RetrofitClient.retrofit.create(YoutubeApiService::class.java)
    }
}