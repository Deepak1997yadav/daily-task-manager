package com.dailytaskmanager.app.presentation.ui.task_form

object DateTimeUtil {
    fun parseDateTime(date: String, time: String): Long? {
        if (date.isBlank()) return null
        val parts = date.split("-")
        if (parts.size != 3) return null
        val tParts = if (time.isNotBlank()) time.split(":") else listOf("0", "0")
        return try {
            val cal = java.util.Calendar.getInstance().apply {
                set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), tParts[0].toInt(), tParts[1].toInt(), 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        } catch (_: Exception) { null }
    }
}