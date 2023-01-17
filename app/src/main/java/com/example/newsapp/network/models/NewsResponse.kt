package com.example.newsapp.network.models

data class NewsResponse(
    val status: String,
    val totalResult: Int,
    val articles: List<Article>
)
