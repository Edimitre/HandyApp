package com.edimitre.handyapp.data.scraper

import android.content.Context
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

    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {

                scrapJetaOshQef()

            }

        }


        return Result.success()
    }


    suspend fun scrapJetaOshQef() {
        var htmlPage: Document? = null


        // ngarko faqen html si dokument
        try {
            htmlPage = Jsoup.connect("https://joq-albania.com/kategori/lajme.html").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(htmlPage != null)
        val newsSection = htmlPage!!.getElementsByClass("home-category-post")
        for (li in newsSection.select("a")) {
            val link = "https://joq-albania.com" + li.attr("href")
            populateNewsFromLink(link)
        }
    }

    private suspend fun populateNewsFromLink(link: String) {
        var htmlPage: Document? = null

        // ngarko faqen html si dokument
        try {
            htmlPage = Jsoup.connect(link).get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(htmlPage != null)
        val title = htmlPage!!.select("h1").text()
        val paragraph = htmlPage.select("p").text()

        val news = News(0, "joq.al", link, title, paragraph, false)


        val newsDao = HandyDb.getInstance(ctx).getNewsDao()

        newsDao.insert(news)
    }


}