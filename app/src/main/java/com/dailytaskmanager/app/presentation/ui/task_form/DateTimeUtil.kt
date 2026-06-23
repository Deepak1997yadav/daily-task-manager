package com.dailytaskmanager.app.presentation.ui.task_form

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun parseDateTime(date: String, time: String): Long? {
        if (date.isBlank()) return null
        val parts = date.split("-")
        if (parts.size != 3) return null
        val tParts = if (time.isNotBlank()) time.split(":") else listOf("0", "0")
        return try {
            val cal = Calendar.getInstance().apply {
                set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), tParts[0].toInt(), tParts[1].toInt(), 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        } catch (_: Exception) { null }
    }

    fun formatDate(millis: Long): String =
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))

    fun formatTime(millis: Long): String =
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(millis))

    fun dateOnly(millis: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun replaceDate(full: Long, newDate: Long): Long {
        val fCal = Calendar.getInstance().apply { timeInMillis = full }
        val dCal = Calendar.getInstance().apply { timeInMillis = newDate }
        fCal.set(Calendar.YEAR, dCal.get(Calendar.YEAR))
        fCal.set(Calendar.MONTH, dCal.get(Calendar.MONTH))
        fCal.set(Calendar.DAY_OF_MONTH, dCal.get(Calendar.DAY_OF_MONTH))
        return fCal.timeInMillis
    }

    fun replaceTime(full: Long, hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = full }
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}