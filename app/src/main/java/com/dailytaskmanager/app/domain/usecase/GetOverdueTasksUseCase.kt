package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetOverdueTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(now: Long): Flow<List<Task>> {
        return repository.getOverdueTasks(now)
    }
}