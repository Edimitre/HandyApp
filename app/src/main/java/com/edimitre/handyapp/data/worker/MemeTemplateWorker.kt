package com.edimitre.handyapp.data.worker

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SharedPrefUtil
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import kotlinx.coroutines.delay
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream


class MemeTemplateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    var memeTemplateDao = HandyDb.getInstance(ctx).getMemeTemplateDao()

    override suspend fun doWork(): Result {

        try{
            val sharedPrefUtil = SharedPrefUtil(ctx)

            val memeTemplateList = sharedPrefUtil.getMemeTemplateList()

            if(memeTemplateList != null && memeTemplateList.isNotEmpty()){

                memeTemplateList.forEach {
                        memeTemplate ->  memeTemplateDao.save(memeTemplate)
                }
            }


            return Result.success()
        }catch (e:Exception){

            return Result.failure()
        }



    }



}

