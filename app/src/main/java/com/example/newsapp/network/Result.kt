package com.example.newsapp.network

sealed class Result<out T>{
    data class Success<out T>(val result: T): Result<T>()
    object Loading: Result<Nothing>()
    data class Error<out T>(val exception: Throwable? = null,val message: String = ""): Result<T>()
}
