package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksByCategoryUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(category: String): Flow<List<Task>> {
        return repository.getTasksByCategory(category)
    }
}