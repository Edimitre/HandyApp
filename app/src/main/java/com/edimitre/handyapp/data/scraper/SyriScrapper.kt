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


class SyriScrapper(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context


    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {


                scrapSyriNet()

            }

        }


        return Result.success()
    }


    private suspend fun scrapSyriNet() {
        var html_page: Document? = null


        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect("https://www.syri.net/lajme/").get()

            assert(html_page != null)
            val newsSection = html_page!!.getElementsByClass("container")
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
        var html_page: Document? = null

        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect(link).get()

            assert(html_page != null)
            val title = html_page.getElementsByClass("ndaje-pak").select("h1").text()
                .replace("Komentet", "")
            val paragraph = html_page.getElementsByClass("ndaje-pak").select("p").text()

            val news = News(0, "syri.net", link, title, paragraph, false)
            val newsDao = HandyDb.getInstance(ctx).getNewsDao()

            newsDao.insert(news)
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }

    }

}