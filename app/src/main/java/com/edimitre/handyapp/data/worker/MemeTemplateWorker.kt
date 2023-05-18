package com.edimitre.handyapp.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SharedPrefUtil
import com.edimitre.handyapp.data.util.SystemService


class MemeTemplateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    var memeTemplateDao = HandyDb.getInstance(ctx).getMemeTemplateDao()

    override suspend fun doWork(): Result {

        try {
            val sharedPrefUtil = SharedPrefUtil(ctx)

            val memeTemplateList = sharedPrefUtil.getMemeTemplateList()

            if (memeTemplateList != null && memeTemplateList.isNotEmpty()) {

                memeTemplateList.forEach { memeTemplate ->
                    memeTemplateDao.save(memeTemplate)
                }
            }


            return Result.success()
        } catch (e: Exception) {

            return Result.failure()
        }


    }


}

