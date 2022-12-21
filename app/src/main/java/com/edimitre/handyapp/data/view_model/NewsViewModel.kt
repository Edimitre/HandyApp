package com.edimitre.handyapp.data.view_model


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.service.NewsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(private val newsService: NewsService) : ViewModel() {


    fun likeNews(news: News): Job = viewModelScope.launch {
        news.liked = true
        newsService.saveNews(news)

    }

    fun deleteNews(news: News): Job = viewModelScope.launch {


        newsService.deleteNews(news)

    }

    suspend fun getOneBySource(source: String): News? {

        return newsService.getOneBySource(source)
    }

    suspend fun deleteAllBySource(source: String):Job = viewModelScope.launch {

        newsService.deleteAllBySource(source)
    }

    fun getAllLikedNewsPaged(): Flow<PagingData<News>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { newsService.getAllLikedNewsPaged()!! })
            .flow
            .cachedIn(viewModelScope)
    }

    fun getAllNewsBySourcePaged(source: String): Flow<PagingData<News>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { newsService.getNewsBySourcePaged(source)!! })
            .flow
            .cachedIn(viewModelScope)
    }

}