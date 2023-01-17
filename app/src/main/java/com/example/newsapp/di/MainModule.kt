package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.network.ApiService
import com.example.newsapp.network.AuthorizationInterceptor
import com.example.newsapp.repository.IMainRepository
import com.example.newsapp.repository.MainRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authorizationInterceptor: AuthorizationInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providerFirebaseReference(): DatabaseReference =
        Firebase.database.getReference("news")

    @Provides
    @Singleton
    fun providerFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun providerFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    @Singleton
    fun providerMainRepository(
        apiService: ApiService,
        databaseReference: DatabaseReference,
        auth: FirebaseAuth,
        analytics: FirebaseAnalytics
    ): IMainRepository{
        return MainRepository(apiService,databaseReference,auth,analytics)
    }
}