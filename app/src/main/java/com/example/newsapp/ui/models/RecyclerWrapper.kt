package com.example.newsapp.ui.models

data class RecyclerWrapper<T>(
    val data: T,
    var isFavorite: Boolean = false
)
