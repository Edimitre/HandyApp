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
import org.jsoup.select.Elements
import java.util.stream.Collectors


class BotaAlScrapper(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {


                scrapBotaAl()

            }

        }


        return Result.success()
    }

    private suspend fun scrapBotaAl() {
        var htmlPage: Document? = null
        val linksList = ArrayList<String>()

        // ngarko faqen html si dokument
        try {
            htmlPage = Jsoup.connect("https://bota.al/").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(htmlPage != null)
        val newsSection: Elements = htmlPage!!.getElementsByClass("background-overlay")


        for (links in newsSection.select("a")) {
            val link: String = links.attr("href")
            if (link.length > 40) {
                linksList.add(link)
            }
        }
        val links: List<String>? = linksList.stream().distinct().collect(Collectors.toList())
        var i = 0
        for (link in links!!) {
            populateNewsFromLink(link)
            i += 1
            if (i == 25) {
                break
            }
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
        val title: String = htmlPage!!.select("h1").text()
        val paragraph: String = htmlPage.select("p").text()

        val news = News(0, "bota.al", link, title, paragraph, false)


        val newsDao = HandyDb.getInstance(ctx).getNewsDao()

        newsDao.insert(news)

    }


}