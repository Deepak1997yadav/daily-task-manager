package com.dailytaskmanager.app.domain.usecase

data class TaskUseCases(
    val addTask: AddTaskUseCase,
    val updateTask: UpdateTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val getAllTasks: GetAllTasksUseCase,
    val getTaskById: GetTaskByIdUseCase,
    val getTasksByCategory: GetTasksByCategoryUseCase,
    val getTasksDueBetween: GetTasksDueBetweenUseCase,
    val getOverdueTasks: GetOverdueTasksUseCase,
    val getTasksByAssignee: GetTasksByAssigneeUseCase,
    val getPendingReminders: GetPendingRemindersUseCase,
    val markNotified: MarkNotifiedUseCase
)
