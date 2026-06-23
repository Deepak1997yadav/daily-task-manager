package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository

class AddTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Int {
        return repository.insertTask(task).toInt()
    }
}
