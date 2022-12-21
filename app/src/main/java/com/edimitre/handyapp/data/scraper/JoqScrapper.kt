package com.edimitre.handyapp.data.scraper

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.room_database.HandyDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class JoqScrapper(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context


    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {

                scrapJetaOshQef()

            }

        }


        return Result.success()
    }


    suspend fun scrapJetaOshQef() {
        var html_page: Document? = null


        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect("https://joq-albania.com/kategori/lajme.html").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(html_page != null)
        val newsSection = html_page!!.getElementsByClass("home-category-post")
        for (li in newsSection.select("a")) {
            val link = "https://joq-albania.com" + li.attr("href")
            populateNewsFromLink(link)
        }
    }

    suspend fun populateNewsFromLink(link: String) {
        var html_page: Document? = null

        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect(link).get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(html_page != null)
        val title = html_page!!.select("h1").text()
        val paragraph = html_page.select("p").text()

        val news = News(0, "joq.al", link, title, paragraph, false)


        val newsDao = HandyDb.getInstance(ctx).getNewsDao()

        newsDao.insert(news)
    }


}