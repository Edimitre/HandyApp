package com.edimitre.handyapp.data.worker

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.edimitre.handyapp.HandyAppEnvironment
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


    private val storageDirectory =
        File("${Environment.getExternalStorageDirectory()}/${HandyAppEnvironment.FILES_STORAGE_DIRECTORY}")


    override suspend fun doWork(): Result {

        return doWorkFromDb()

    }


    private suspend fun doWorkFromDb(): Result {

        setProgress(workDataOf("isRunning" to true))
        delay(1000)

        systemService.setNotification("STARTED", 0, true)


        delay(2000)

        val workDayDao = HandyDb.getInstance(ctx).getWorkDayDao()

        val workDayList = workDayDao.getAllWorkDaysByYearMonth(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth()
        )

        val fileName =
            "dt_" + TimeUtils().getCurrentDate() + "_" + TimeUtils().getCurrentMonth() + "_" + TimeUtils().getCurrentYear() + ".xls"



        return if (workDayList != null && workDayList.isNotEmpty() && storageDirectory.exists()) {

            val workBook = getXmlSheet(workDayList)

            systemService.setNotification("WORKING...", 30, true)

            delay(2000)
            createFile(workBook, storageDirectory, fileName)

            systemService.setNotification("CREATING FILE", 70, true)


            delay(2000)

            systemService.setNotification("FILE CREATED WITH SUCCESS", 100, false)

            delay(2000)

            setProgress(workDataOf("isRunning" to false))
            Result.success()


        } else {

            systemService.setNotification("FAILURE", 100, false)


            setProgress(workDataOf("isRunning" to false))
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


}

