package com.dailytaskmanager.app.data.local

import androidx.room.*
import com.dailytaskmanager.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY timestamp DESC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate BETWEEN :start AND :end ORDER BY dueDate ASC")
    fun getTasksDueBetween(start: Long, end: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDone = 0 AND dueDate IS NOT NULL AND dueDate < :now ORDER BY dueDate ASC")
    fun getOverdueTasks(now: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE assignee LIKE '%' || :name || '%' ORDER BY timestamp DESC")
    fun getTasksByAssignee(name: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE reminderTime IS NOT NULL AND reminderTime > :after AND isNotified = 0")
    suspend fun getPendingReminders(after: Long): List<TaskEntity>

    @Query("UPDATE tasks SET isNotified = 1 WHERE id = :id")
    suspend fun markNotified(id: Int)
}
