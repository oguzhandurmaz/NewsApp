package com.example.newsapp.repository

import androidx.core.os.bundleOf
import com.example.newsapp.network.ApiService
import com.example.newsapp.network.Result
import com.example.newsapp.network.models.Article
import com.example.newsapp.network.models.Source
import com.example.newsapp.utils.Utils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepository constructor(
    private val apiService: ApiService,
    private val ref: DatabaseReference,
    private val auth: FirebaseAuth,
    private val analytics: FirebaseAnalytics
): IMainRepository {

    override suspend fun signIn(): AuthResult? {
        return try {
            val result = auth.signInAnonymously().await()
            result
        } catch (t: Throwable) {
            null
        }
    }

    override suspend fun getNews(
        query: String,
        category: String,
        country: String
    ): Result<List<Article>> {
        return try {
            val result = apiService.getNews(query, category, country)
            if (result.status != "ok") {
                return Result.Error()
            }
            Result.Success(result.articles)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    override suspend fun getFavoriteNews(): Result<List<Article>> {
        return try {
            if (!isSignIn()) return Result.Error()
            val newsSnapshot = ref.child(getUserId()).get().await()
            val news = mutableListOf<Article>()
            for (child in newsSnapshot.children) {
                val article = snapshotToArticle(child)
                article?.let { news.add(it) }
            }
            Result.Success(news)
        } catch (t: Throwable) {
            Result.Error()
        }
    }

    override suspend fun getFavoriteNew(id: String): Article? {
        return try {
            val result = ref.child(getUserId()).child(id).get().await()
            if (result.value == null) return null
            val article = snapshotToArticle(result)
            article
        } catch (t: Throwable) {
            null
        }

    }

    override suspend fun addToFavorite(news: Article): Result<Article> {
        return try {
            if (!isSignIn()) return Result.Error()
            val id = Utils.getIdFromTitle(news.title)
            if (getFavoriteNew(id) != null) return Result.Error()
            val result = ref.child(getUserId()).child(id).setValue(news).await()
            analytics.logEvent(
                FirebaseAnalytics.Event.SELECT_ITEM,
                bundleOf(FirebaseAnalytics.Param.ITEM_NAME to news.title)
            )
            Result.Success(news)
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }

    override fun getFavoritesIds() = channelFlow<List<String>> {
        if (!isSignIn()) send(emptyList())
        ref.child(getUserId()).snapshots.collectLatest { snapshot ->
            val ids = mutableListOf<String>()
            for (child in snapshot.children) {
                child.key?.let { it -> ids.add(it) }
            }
            send(ids)
        }
    }

    private fun isSignIn() = getUserId() != ""

    private fun getUserId(): String {
        return Firebase.auth.currentUser?.uid ?: run { "" }
    }

    //fun getIdFromTitle(title: String) = title.replace("[^A-Za-z0-9]".toRegex(), "")
    private fun snapshotToArticle(child: DataSnapshot): Article? {
        return try {
            val title = child.child("title").getValue(String::class.java) ?: ""
            val sourceHashMap = child.child("source")
            val id = sourceHashMap.child("id").getValue(String::class.java) ?: ""
            val name = sourceHashMap.child("name").getValue(String::class.java) ?: ""
            val source = Source(id, name)
            val author = child.child("author").getValue(String::class.java) ?: ""
            val description = child.child("description").getValue(String::class.java) ?: ""
            val url = child.child("url").getValue(String::class.java) ?: ""
            val urlToImage = child.child("urlToImage").getValue(String::class.java) ?: ""
            val publishedAt = child.child("publishedAt").getValue(String::class.java) ?: ""
            val content = child.child("content").getValue(String::class.java) ?: ""
            Article(
                child.key ?: "",
                source,
                author,
                title,
                description,
                url,
                urlToImage,
                publishedAt,
                content
            )
        } catch (t: Throwable) {
            null
        }

    }
}