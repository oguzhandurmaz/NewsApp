package com.example.newsapp.viewmodel

import androidx.lifecycle.*
import com.example.newsapp.network.Result
import com.example.newsapp.network.models.Article
import com.example.newsapp.repository.IMainRepository
import com.example.newsapp.repository.MainRepository
import com.example.newsapp.ui.models.RecyclerWrapper
import com.example.newsapp.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: IMainRepository) : ViewModel() {

    private val _newsResult = MutableLiveData<Result<List<Article>>>()
    val newsResult: LiveData<Result<List<Article>>> get() = _newsResult

    private val _favoriteNewsResult = MutableLiveData<Result<List<Article>>>()
    val favoriteNewsResult: LiveData<Result<List<Article>>> get() = _favoriteNewsResult

    private val _addFavoriteResult = Channel<Result<Article>>()
    val addFavoriteResult: Flow<Result<Article>> get() = _addFavoriteResult.receiveAsFlow()


    val favoriteIds = mainRepository.getFavoritesIds().asLiveData()

    fun signIn() = viewModelScope.launch {
        val result = mainRepository.signIn()
    }

    fun getNews(query: String, category: String = "general") = viewModelScope.launch {
        if (_newsResult.value is Result.Success) return@launch
        _newsResult.value = Result.Loading
        _newsResult.value = mainRepository.getNews(query, category)
    }

    fun getFavoriteNews() = viewModelScope.launch {
        _favoriteNewsResult.value = Result.Loading
        _favoriteNewsResult.value = mainRepository.getFavoriteNews()
    }

    fun getRecyclerItems(data: List<Article>): List<RecyclerWrapper<Article>> {
        return data.map {
            RecyclerWrapper(
                it,
                favoriteIds.value?.contains(Utils.getIdFromTitle(it.title)) ?: false
            )
        }
    }

    fun addToFavorite(news: Article) = viewModelScope.launch {
        _addFavoriteResult.send(Result.Loading)
        _addFavoriteResult.send(mainRepository.addToFavorite(news))
    }

}