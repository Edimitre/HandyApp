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


class SyriScrapper(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context


    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {


                scrapSyriNet()

            }

        }


        return Result.success()
    }


    private suspend fun scrapSyriNet() {
        val htmlPage: Document?


        // ngarko faqen html si dokument
        try {
            htmlPage = Jsoup.connect("https://www.syri.net/lajme/").get()

            assert(htmlPage != null)
            val newsSection = htmlPage!!.getElementsByClass("container")
            for (links in newsSection.select("a")) {
                val link = links.attr("href")
                if (link.length > 45) {
                    populateNewsFromLink(link)
                }
            }
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }

    }

    private suspend fun populateNewsFromLink(link: String) {
        val htmlPage: Document?

        // ngarko faqen html si dokument
        try {
            htmlPage = Jsoup.connect(link).get()

            assert(htmlPage != null)
            val title = htmlPage.getElementsByClass("ndaje-pak").select("h1").text()
                .replace("Komentet", "")
            val paragraph = htmlPage.getElementsByClass("ndaje-pak").select("p").text()

            val news = News(0, "syri.net", link, title, paragraph, false)
            val newsDao = HandyDb.getInstance(ctx).getNewsDao()

            newsDao.insert(news)
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }

    }

}