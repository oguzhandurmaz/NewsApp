package com.example.newsapp.network.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(
    val id: String,
    val name: String
): Parcelable {
    override fun hashCode(): Int {
        return super.hashCode()
    }
}
