package com.dailytaskmanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailytaskmanager.app.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isDone: Boolean = false,
    val timestamp: Long
)

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        isDone = isDone,
        timestamp = timestamp
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        isDone = isDone,
        timestamp = timestamp
    )
}