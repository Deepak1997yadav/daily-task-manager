package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository

class GetTaskByIdUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Int): Task? {
        return repository.getTaskById(id)
    }
}
