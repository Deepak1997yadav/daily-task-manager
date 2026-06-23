package com.dailytaskmanager.app.domain.repository

import com.dailytaskmanager.app.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTaskById(id: Int): Task?
    fun getTasksByCategory(category: String): Flow<List<Task>>
    fun getTasksDueBetween(start: Long, end: Long): Flow<List<Task>>
    fun getOverdueTasks(now: Long): Flow<List<Task>>
    fun getTasksByAssignee(name: String): Flow<List<Task>>
    suspend fun getPendingReminders(after: Long): List<Task>
    suspend fun markNotified(id: Int)
}
