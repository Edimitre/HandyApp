package com.edimitre.handyapp.data.worker

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.data.room_database.HandyDb
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils
import java.io.File


class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val ctx = context
    val systemService = SystemService(ctx)


    private val fileName =
        "dt_" + TimeUtils().getCurrentDate() + "_" + TimeUtils().getCurrentMonth() + "_" + TimeUtils().getCurrentYear() + ".xls"

    private val storageDirectory =
        File("${Environment.getExternalStorageDirectory()}/${HandyAppEnvironment.FILES_STORAGE_DIRECTORY}")

    val file = File("${storageDirectory}/${fileName} ")


    override suspend fun doWork(): Result {

        val auth = HandyDb.getInstance(ctx).getAuthDao().getAuthModelOnCoroutine()
        val workDay = HandyDb.getInstance(ctx).getWorkDayDao().getWorkDayByYearMonthDay(
            TimeUtils().getCurrentYear(),
            TimeUtils().getCurrentMonth(),
            TimeUtils().getCurrentDate()
        )
        val hourNow = TimeUtils().getCurrentHour()

        if (auth!!.isNotificationEnabled && hourNow in 9..22) {

            systemService.notify(HandyAppEnvironment.TITLE, "Are any forgotten expenses ? ")
        }

        if (auth.isWorkNotificationEnabled && hourNow in 19..23 && workDay == null && !TimeUtils().isSaturdayOrSunday()) {
            systemService.notify(
                HandyAppEnvironment.TITLE,
                "Workday missing for today..please generate one ? "
            )
        }

        return Result.success()
    }

}
