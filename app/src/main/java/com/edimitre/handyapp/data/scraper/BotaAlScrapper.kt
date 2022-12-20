package com.edimitre.handyapp.data.scraper

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.edimitre.handyapp.data.model.News
import com.edimitre.handyapp.data.room_database.HandyDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.stream.Collectors


class BotaAlScrapper(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    val output: Data.Builder = Data.Builder()

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {

                val newsDao = HandyDb.getInstance(ctx).getNewsDao()

                val oneNews = newsDao.getOne()

                when (oneNews) {
                    null -> {
                        scrapBotaAl()
                    }
                    else -> {
                        newsDao.deleteAllNews().also { scrapBotaAl() }

                    }
                }

            }

        }


        return Result.success()
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private suspend fun scrapBotaAl() {
        var html_page: Document? = null
        val linksList = ArrayList<String>()

        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect("https://bota.al/").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(html_page != null)
        val newsSection: Elements = html_page!!.getElementsByClass("background-overlay")


        for (links in newsSection.select("a")) {
            val link: String = links.attr("href")
            if (link.length > 40) {
                linksList.add(link)
            }
        }
        var links: List<String>? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            links = linksList.stream().distinct().collect(Collectors.toList())
        }
        var i = 0
        for (link in links!!) {
            populateNewsFromLink(link)
            i += 1
            if (i == 20) {
                return
            }
        }
    }

    private suspend fun populateNewsFromLink(link: String) {
        var html_page: Document? = null


        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect(link).get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(html_page != null)
        val title: String = html_page!!.select("h1").text()
        val paragraph: String = html_page.select("p").text()

        val news = News(0, "bota.al", link, title, paragraph)


        val newsDao = HandyDb.getInstance(ctx).getNewsDao()

        newsDao.insert(news)

    }


    private fun sendStatus(status: String, workStatus: String) {

        output.putString(status, workStatus)
            .build()
    }

}