package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksDueBetweenUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(start: Long, end: Long): Flow<List<Task>> {
        return repository.getTasksDueBetween(start, end)
    }
}