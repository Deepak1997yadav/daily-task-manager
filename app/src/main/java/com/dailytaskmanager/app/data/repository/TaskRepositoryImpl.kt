package com.dailytaskmanager.app.data.repository

import com.dailytaskmanager.app.data.local.TaskDao
import com.dailytaskmanager.app.data.local.entity.toEntity
import com.dailytaskmanager.app.data.local.entity.toTask
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val dao: TaskDao
) : TaskRepository {

    override suspend fun insertTask(task: Task) {
        dao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        dao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        dao.deleteTask(task.toEntity())
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks().map { it.map { entity -> entity.toTask() } }
    }

    override suspend fun getTaskById(id: Int): Task? {
        return dao.getTaskById(id)?.toTask()
    }
}
