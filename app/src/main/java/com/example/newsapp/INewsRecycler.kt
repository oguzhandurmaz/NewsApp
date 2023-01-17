package com.example.newsapp

import com.example.newsapp.network.models.Article

interface INewsRecycler {
    fun onClickListener(data: Article){}
    fun onFavoriteListener(data: Article){}
}