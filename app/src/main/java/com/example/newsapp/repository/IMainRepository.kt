package com.example.newsapp.repository

import com.example.newsapp.network.Result
import com.example.newsapp.network.models.Article
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface IMainRepository {
    suspend fun signIn(): AuthResult?
    suspend fun getNews(
        query: String,
        category: String,
        country: String = "us"
    ): Result<List<Article>>
    suspend fun getFavoriteNews(): Result<List<Article>>
    suspend fun getFavoriteNew(id: String): Article?
    suspend fun addToFavorite(news: Article): Result<Article>
    fun getFavoritesIds() : Flow<List<String>>
}