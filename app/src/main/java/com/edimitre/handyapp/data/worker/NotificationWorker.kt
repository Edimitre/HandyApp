package com.edimitre.handyapp.data.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.edimitre.handyapp.HandyAppEnvironment
import com.edimitre.handyapp.HandyAppEnvironment.TAG
import com.edimitre.handyapp.data.util.SystemService
import com.edimitre.handyapp.data.util.TimeUtils


class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    val systemService = SystemService(context)

    override fun doWork(): Result {

        Log.e(TAG, "on notification worker ", )
        val hourNow = TimeUtils().getCurrentHour()

        when {
            hourNow > 9 -> {
                systemService.notify(HandyAppEnvironment.TITLE, "Are any forgotten expenses ? ")
            }
            hourNow > 12 -> {
                systemService.notify(HandyAppEnvironment.TITLE, "Don't lose track of expenses ? ")
            }
            hourNow > 18 -> {
                systemService.notify(
                    HandyAppEnvironment.TITLE,
                    "It's afternoon !! Any expense made ?"
                )
            }
            hourNow > 22 -> {
                systemService.notify(HandyAppEnvironment.TITLE, "New expenses ? ")
            }
        }

        return Result.success()
    }


}
