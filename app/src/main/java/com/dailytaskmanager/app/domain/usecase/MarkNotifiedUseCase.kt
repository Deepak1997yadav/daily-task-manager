package com.dailytaskmanager.app.domain.usecase

import com.dailytaskmanager.app.domain.repository.TaskRepository

class MarkNotifiedUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.markNotified(id)
    }
}