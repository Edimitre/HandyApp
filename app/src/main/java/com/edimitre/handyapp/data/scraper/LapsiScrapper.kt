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


class LapsiScrapper(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context



    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {

                scrapLapsiAl1().also { scrapLapsiAl2() }


            }

        }


        return Result.success()
    }


    private suspend fun scrapLapsiAl1() {
        var htmlPage: Document? = null


        // ngarko faqen html si dokument
        try {
            htmlPage = Jsoup.connect("https://lapsi.al/kategoria/lajme/").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(htmlPage != null)
        val newsSection = htmlPage!!.getElementsByClass("post-preview")
        for (links in newsSection.select("a")) {
            val link = links.attr("href")
            populateNewsFromLink(link)
        }
    }

    private suspend fun scrapLapsiAl2() {
        var htmlPage: Document? = null


        // ngarko faqen html si dokument
        try {
            htmlPage = Jsoup.connect("https://lapsi.al/kategoria/kryesore/").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(htmlPage != null)
        val newsSection = htmlPage!!.getElementsByClass("post-preview")
        for (links in newsSection.select("a")) {
            val link = links.attr("href")
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
        val paragraph = htmlPage.select("p").text().replace("identifikohu", "")
            .replace("Adresa juaj email s’do të bëhet publike. Koment Emër Email Sajt", "")


        val news = News(0, "lapsi.al", link, title, paragraph, false)


        val newsDao = HandyDb.getInstance(ctx).getNewsDao()

        newsDao.insert(news)
    }


}