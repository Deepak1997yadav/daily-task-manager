package com.dailytaskmanager.app

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow



class FakeTaskRepository : TaskRepository {

    private val taskList = mutableListOf<Task>()
    private val flow = MutableStateFlow<List<Task>>(emptyList())

    override suspend fun insertTask(task: Task) {
        taskList.add(task)
        flow.value = taskList
    }

    override suspend fun updateTask(task: Task) {
        taskList.replaceAll { if (it.id == task.id) task else it }
        flow.value = taskList
    }

    override suspend fun deleteTask(task: Task) {
        taskList.removeIf { it.id == task.id }
        flow.value = taskList
    }

    override fun getAllTasks(): Flow<List<Task>> = flow

    override suspend fun getTaskById(id: Int): Task? {
        return taskList.find { it.id == id }
    }

    fun setTasks(tasks: List<Task>) {
        taskList.clear()
        taskList.addAll(tasks)
        flow.value = taskList
    }
}
