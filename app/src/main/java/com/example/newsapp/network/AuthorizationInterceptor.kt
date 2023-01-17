package com.example.newsapp.network

import com.example.newsapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val requestBuilder = original.newBuilder()
            .header("Authorization",BuildConfig.NEWSAPIKEY)
        val request = requestBuilder.build()
        return chain.proceed(request)

    }
}