package com.edimitre.handyapp.data.service


import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.NewsDao
import com.edimitre.handyapp.data.model.News
import javax.inject.Inject

class NewsService @Inject constructor(private val newsDao: NewsDao) {


    suspend fun saveNews(news: News) {
        newsDao.insert(news)

    }

    suspend fun deleteNews(news: News) {

        newsDao.delete(news)
    }

    suspend fun getOneBySource(source: String): News? {
        return newsDao.getOneBySource(source)
    }

    suspend fun deleteAllBySource(source: String) {

        return newsDao.deleteAllNewsBySource(source)
    }

    fun getAllLikedNewsPaged(): PagingSource<Int, News>? {

        return newsDao.getAllLikedNewsPaged()
    }

    fun getNewsBySourcePaged(source: String): PagingSource<Int, News>? {
        return newsDao.getNewsBySourcePaged(source)
    }

}