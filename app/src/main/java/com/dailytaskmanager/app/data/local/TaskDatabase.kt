package com.dailytaskmanager.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dailytaskmanager.app.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
