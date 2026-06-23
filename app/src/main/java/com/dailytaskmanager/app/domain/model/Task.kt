package com.dailytaskmanager.app.domain.model

data class Task(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val assignee: String = "",
    val priority: Int = 2,
    val category: String = "general",
    val reminderTime: Long? = null,
    val repeatInterval: Long? = null,
    val isNotified: Boolean = false,
    val stopwatchElapsed: Long = 0L,
    val stopwatchRunning: Boolean = false,
    val stopwatchLastStart: Long = 0L
)
