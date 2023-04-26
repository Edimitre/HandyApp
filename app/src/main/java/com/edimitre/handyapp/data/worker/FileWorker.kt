package com.edimitre.handyapp.data.worker

import android.content.Context
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.R
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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



        val workDayDao = HandyDb.getInstance(ctx).getWorkDayDao()

        val workDayList = workDayDao.getAllWorkDaysForPrinting(TimeUtils().getCurrentYear(), TimeUtils().getCurrentMonth())

        setForegroundAsync(createForegroundInfo("STARTING"))


        if(workDayList != null && workDayList.isNotEmpty()){





            var i = 0
            val workBook = HSSFWorkbook()

            val title = "" + TimeUtils().getCurrentDate() + "_" +TimeUtils().getCurrentMonth() + "_" + TimeUtils().getCurrentYear()
            val sheet = workBook.createSheet(title)




            setForegroundAsync(createForegroundInfo("GENERATING FILE.."))
            delay(2000L)
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


            val folderName = HandyAppEnvironment.FILES_STORAGE_DIRECTORY
            val directory = File("${Environment.getExternalStorageDirectory()}/$folderName")

            if(directory.exists()){

                val fileName = "dt_" + TimeUtils().getCurrentDate() + "_" +TimeUtils().getCurrentMonth() + "_" + TimeUtils().getCurrentYear() + ".xls"

                val file = File(directory.path + File.separator + fileName)

                if(!file.exists()){

                    withContext(Dispatchers.IO) {
                        file.createNewFile()

                        val fso = FileOutputStream(file)
                        workBook.write(fso)

                        fso.flush()
                        fso.close()

                    }



                    setForegroundAsync(createForegroundInfo("FILE CREATED SUCCESSFULLY"))
                    delay(2000L)

                    return Result.success()

                }else{


                    setForegroundAsync(createForegroundInfo("FILE ALREADY EXIST"))

                    delay(2000L)
                    return Result.failure()

                }

            }else{


                setForegroundAsync(createForegroundInfo("FOLDER DOESN'T EXIST\n have you granted permissions?"))
                delay(2000L)

                return Result.failure()
            }



        }else{

            delay(2000L)
            return Result.failure()
        }


    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = HandyAppEnvironment.NOTIFICATION_CHANNEL_ID
        val title = HandyAppEnvironment.TITLE
        val cancel = "CANCEL"
        // This PendingIntent can be used to cancel the worker
//        val intent = WorkManager.getInstance(ctx)
//            .createCancelPendingIntent(getId())


        val notification = NotificationCompat.Builder(ctx, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_settings)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
//            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(HandyAppEnvironment.NOTIFICATION_NUMBER_ID, notification)
    }

}