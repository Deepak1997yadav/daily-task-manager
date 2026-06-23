package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksByAssigneeUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(name: String): Flow<List<Task>> {
        return repository.getTasksByAssignee(name)
    }
}