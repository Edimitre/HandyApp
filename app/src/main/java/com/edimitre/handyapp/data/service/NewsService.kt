package com.edimitre.handyapp.data.service


import androidx.paging.PagingSource
import com.edimitre.handyapp.data.dao.NewsDao
import com.edimitre.handyapp.data.dao.ReminderNotesDao
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.model.Note

import javax.inject.Inject

class NewsService @Inject constructor(private val newsDao: NewsDao) {




    suspend fun saveNews(news: News) {
        newsDao.insert(news)

    }

    suspend fun deleteNews(news: News) {

        newsDao.delete(news)
    }


    fun getAllNewsPaged(): PagingSource<Int, News>? {

        return newsDao.getAllNewsPaged()
    }

    fun getNewsBySourcePaged(source: String): PagingSource<Int, News>? {
        return newsDao.getNewsBySourcePaged(source)
    }

}