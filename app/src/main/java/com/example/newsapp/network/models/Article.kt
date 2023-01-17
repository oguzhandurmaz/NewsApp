package com.example.newsapp.network.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    @Expose
    val firebaseKey: String,
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
): Parcelable {
    override fun hashCode(): Int {
        return super.hashCode()
    }
}
