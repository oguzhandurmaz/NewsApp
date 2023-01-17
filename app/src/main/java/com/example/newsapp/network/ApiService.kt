package com.example.newsapp.network

import com.example.newsapp.network.models.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("top-headlines")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("category") category: String,
        @Query("country") country: String
    ): NewsResponse
}