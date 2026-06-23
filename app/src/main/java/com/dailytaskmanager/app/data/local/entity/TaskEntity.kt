package com.dailytaskmanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailytaskmanager.app.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        isDone = isDone,
        timestamp = timestamp,
        dueDate = dueDate,
        assignee = assignee,
        priority = priority,
        category = category,
        reminderTime = reminderTime,
        repeatInterval = repeatInterval,
        isNotified = isNotified,
        stopwatchElapsed = stopwatchElapsed,
        stopwatchRunning = stopwatchRunning,
        stopwatchLastStart = stopwatchLastStart
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        isDone = isDone,
        timestamp = timestamp,
        dueDate = dueDate,
        assignee = assignee,
        priority = priority,
        category = category,
        reminderTime = reminderTime,
        repeatInterval = repeatInterval,
        isNotified = isNotified,
        stopwatchElapsed = stopwatchElapsed,
        stopwatchRunning = stopwatchRunning,
        stopwatchLastStart = stopwatchLastStart
    )
}