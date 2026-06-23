package com.dailytaskmanager.app.data.local

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dailytaskmanager.app.domain.model.Task

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("task_id", 0)
        val title = intent.getStringExtra("task_title") ?: "Task Reminder"

        val notification = NotificationCompat.Builder(context, ReminderScheduler.REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Task Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(taskId, notification)
        } catch (_: SecurityException) { }
    }
}

object ReminderScheduler {

    const val REMINDER_CHANNEL_ID = "task_reminders"

    fun createNotificationChannel(context: Context) {
        val channel = android.app.NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Task Reminders",
            android.app.NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders for daily tasks"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun scheduleReminder(context: Context, task: Task) {
        val reminderTime = task.reminderTime ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("task_id", task.id)
            putExtra("task_title", task.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, task.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
    }

    fun cancelReminder(context: Context, taskId: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, taskId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}