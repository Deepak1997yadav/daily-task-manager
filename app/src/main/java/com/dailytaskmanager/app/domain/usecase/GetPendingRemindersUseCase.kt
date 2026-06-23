package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.repository.TaskRepository

class GetPendingRemindersUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(after: Long): List<Task> {
        return repository.getPendingReminders(after)
    }
}