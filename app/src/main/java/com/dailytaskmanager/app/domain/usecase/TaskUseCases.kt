package com.dailytaskmanager.app.domain.usecase

data class TaskUseCases(
    val addTask: AddTaskUseCase,
    val updateTask: UpdateTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val getAllTasks: GetAllTasksUseCase,
    val getTaskById: GetTaskByIdUseCase
)
