package com.dailytaskmanager.app

import android.app.Application
import com.dailytaskmanager.app.data.local.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DailytaskmanagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ReminderScheduler.createNotificationChannel(this)
    }
}
