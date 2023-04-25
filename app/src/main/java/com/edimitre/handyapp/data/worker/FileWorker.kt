package com.edimitre.handyapp.data.worker

import android.content.Context
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.model.Reminder
import com.edimitre.handyapp.data.model.WorkDay
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream


class FileWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context

    var systemService = SystemService(ctx)

    override suspend fun doWork(): Result {

        setForegroundAsync(createForegroundInfo("STARTING"))

        val workDayDao = HandyDb.getInstance(ctx).getWorkDayDao()

        val workDayList = workDayDao.getAllWorkDaysForPrinting(TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth())

        if(workDayList != null && workDayList.isNotEmpty()){

            setForegroundAsync(createForegroundInfo("FOUND DATA"))

            return if(createExelFile(workDayList)){

                setForegroundAsync(createForegroundInfo("FILE CREATED SUCCESSFULLY"))


                Result.success()
            }else{

                setForegroundAsync(createForegroundInfo("FAILED TO CREATE FILE"))

                Result.failure()
            }


        }else{

            return Result.failure()
        }
    }




    private fun createExelFile(workDayList:List<WorkDay>):Boolean{

        val fileCreated:Boolean

        var i = 0
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet()


        workDayList.forEach { wd ->

            val row = sheet.createRow(i)

            val dateCell: HSSFCell = row.createCell(0)
            val timeInMilliSeconds = TimeUtils().getTimeInMilliSeconds(wd.year, wd.month, wd.day)
            val dateString = TimeUtils().getDateStringFromMilliSeconds(timeInMilliSeconds)

            dateCell.setCellValue("data $dateString")


            val hoursCell: HSSFCell = row.createCell(1)
            hoursCell.setCellValue("hours ${wd.workHours}")

            val dayActivityCell: HSSFCell = row.createCell(2)
            dayActivityCell.setCellValue("activity ${wd.activity}" )


            i++
        }


        val folderName = "WORK_FOLDER"
        val directory = File("${Environment.getExternalStorageDirectory()}/$folderName")

        if(directory.exists()){

            val fileName = "demoFile.xls"

            val file = File(directory.path + File.separator + fileName)

            if(!file.exists()){
                file.createNewFile()
            }

            val fso = FileOutputStream(file)

            workBook.write(fso)

            fso.flush()
            fso.close()

            fileCreated = true
        }else{

            fileCreated = false
        }

        return fileCreated


    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = HandyAppEnvironment.NOTIFICATION_CHANNEL_ID
        val title = HandyAppEnvironment.TITLE
        val cancel = "CANCEL"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(ctx)
            .createCancelPendingIntent(getId())


        val notification = NotificationCompat.Builder(ctx, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_settings)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, notification)
    }

}