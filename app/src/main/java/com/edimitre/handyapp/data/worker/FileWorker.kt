package com.edimitre.handyapp.data.worker

import android.app.Notification
import android.content.Context
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import kotlinx.coroutines.delay
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream


class FileWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    private lateinit var notifBuilder: NotificationCompat.Builder

//    var progress = MutableLiveData<Int>(0)

    override suspend fun doWork(): Result {


        setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("STARTING",0,true)))


        delay(2000)

        val workDayDao = HandyDb.getInstance(ctx).getWorkDayDao()

        val workDayList = workDayDao.getAllWorkDaysForPrinting(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth()
        )

        val fileName =
            "dt_" + TimeUtils().getCurrentDate() + "_" + TimeUtils().getCurrentMonth() + "_" + TimeUtils().getCurrentYear() + ".xls"

        val storageDirectory =
            File("${Environment.getExternalStorageDirectory()}/${HandyAppEnvironment.FILES_STORAGE_DIRECTORY}")


        return if (workDayList != null && workDayList.isNotEmpty() && storageDirectory.exists()) {

            val workBook = getXmlSheet(workDayList)
            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("SHEET CREATED",30,true)))

            delay(2000)
            createFile(workBook, storageDirectory, fileName)
            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("FILE CREATED",70,true)))

            delay(2000)
            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("SUCCESS",100,false)))
            delay(2000)

            Result.success()
        } else {

            setForeground(ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, getNotification("FAILURE",100,false)))
            Result.failure()
        }

    }

    private fun getXmlSheet(workDayList: List<WorkDay>): Workbook {

        var i = 0
        val workBook = HSSFWorkbook()

        val title =
            "" + TimeUtils().getCurrentDate() + "_" + TimeUtils().getCurrentMonth() + "_" + TimeUtils().getCurrentYear()
        val sheet = workBook.createSheet(title)

        workDayList.forEach { wd ->

            val row = sheet.createRow(i)

            val dateCell: HSSFCell = row.createCell(0)

//                val timeInMilliSeconds = TimeUtils().getTimeInMilliSeconds(wd.year, wd.month, wd.day)
            dateCell.setCellValue("${wd.day}/${wd.month}/${wd.year}")


            val hoursCell: HSSFCell = row.createCell(1)
            hoursCell.setCellValue("${wd.workHours} ore")

            val dayActivityCell: HSSFCell = row.createCell(2)
            dayActivityCell.setCellValue(wd.activity)


            i++


        }


        return workBook
    }

    private fun createFile(workBook: Workbook, storageDirectory: File, fileName: String) {


        val file = File(storageDirectory.path + File.separator + fileName)

        file.createNewFile()

        val fso = FileOutputStream(file)
        workBook.write(fso)

        fso.flush()
        fso.close()

    }


    private fun getNotification(text:String, progress:Int, onGoing:Boolean): Notification {

        val maxProgress = 100

        notifBuilder =
            NotificationCompat.Builder(ctx, HandyAppEnvironment.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(HandyAppEnvironment.TITLE)
                .setContentText(text)
                .setOngoing(onGoing)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(maxProgress, progress, false)
                .setOnlyAlertOnce(true)

        return notifBuilder.build()
    }

}

