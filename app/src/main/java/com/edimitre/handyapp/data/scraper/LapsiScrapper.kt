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


class LapsiScrapper(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context



    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun doWork(): Result {

        withContext(Dispatchers.Default) {
            launch {

                scrapLapsiAl1().also { scrapLapsiAl2() }



            }

        }


        return Result.success()
    }


    private suspend fun scrapLapsiAl1() {
        var html_page: Document? = null


        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect("https://lapsi.al/kategoria/lajme/").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(html_page != null)
        val newsSection = html_page!!.getElementsByClass("post-preview")
        for (links in newsSection.select("a")) {
            val link = links.attr("href")
            populateNewsFromLink(link)
        }
    }

    private suspend fun scrapLapsiAl2() {
        var html_page: Document? = null


        // ngarko faqen html si dokument
        try {
            html_page = Jsoup.connect("https://lapsi.al/kategoria/kryesore/").get()
        } catch (e: Exception) {
            println("faqja nuk u gjend")
        }
        assert(html_page != null)
        val newsSection = html_page!!.getElementsByClass("post-preview")
        for (links in newsSection.select("a")) {
            val link = links.attr("href")
            populateNewsFromLink(link)
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
        val title = html_page!!.select("h1").text()
        val paragraph = html_page.select("p").text().replace("identifikohu", "")
            .replace("Adresa juaj email s’do të bëhet publike. Koment Emër Email Sajt", "")


        val news = News(0, "lapsi.al", link, title, paragraph)


        val newsDao = HandyDb.getInstance(ctx).getNewsDao()

        newsDao.insert(news)
    }




}