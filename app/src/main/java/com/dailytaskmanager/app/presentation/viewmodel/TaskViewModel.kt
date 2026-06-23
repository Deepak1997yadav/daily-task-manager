package com.dailytaskmanager.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailytaskmanager.app.data.local.ReminderScheduler
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val application: Application
) : AndroidViewModel(application) {

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    private val _filterCategory = MutableStateFlow("all")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            combine(_filterCategory, _searchQuery) { category, query -> Pair(category, query) }
                .flatMapLatest { (category, query) ->
                    val flow = when (category) {
                        "today" -> {
                            val cal = java.util.Calendar.getInstance()
                            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                            cal.set(java.util.Calendar.MINUTE, 0)
                            cal.set(java.util.Calendar.SECOND, 0)
                            val start = cal.timeInMillis
                            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                            val end = cal.timeInMillis
                            taskUseCases.getTasksDueBetween(start, end)
                        }
                        "overdue" -> taskUseCases.getOverdueTasks(System.currentTimeMillis())
                        "meeting" -> taskUseCases.getTasksByCategory("meeting")
                        "project" -> taskUseCases.getTasksByCategory("project")
                        "followUp" -> taskUseCases.getTasksByCategory("followUp")
                        else -> taskUseCases.getAllTasks()
                    }
                    if (query.isBlank()) flow else flow.map { tasks ->
                        tasks.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
                    }
                }
                .collect { tasks -> _taskList.value = tasks }
        }
    }

    fun setFilter(category: String) {
        _filterCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            val id = taskUseCases.addTask(task)
            val savedTask = taskUseCases.getTaskById(id)
            if (savedTask != null && savedTask.reminderTime != null) {
                ReminderScheduler.scheduleReminder(application, savedTask)
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskUseCases.updateTask(task)
            ReminderScheduler.cancelReminder(application, task.id)
            if (task.reminderTime != null) {
                ReminderScheduler.scheduleReminder(application, task)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskUseCases.deleteTask(task)
            ReminderScheduler.cancelReminder(application, task.id)
        }
    }

    fun loadTaskById(id: Int) {
        viewModelScope.launch {
            _selectedTask.value = taskUseCases.getTaskById(id)
        }
    }

    fun clearSelectedTask() {
        _selectedTask.value = null
    }

    fun toggleStopwatch(task: Task) {
        viewModelScope.launch {
            val updated = if (task.stopwatchRunning) {
                val elapsed = task.stopwatchElapsed + (System.currentTimeMillis() - task.stopwatchLastStart)
                task.copy(stopwatchRunning = false, stopwatchElapsed = elapsed, stopwatchLastStart = 0)
            } else {
                task.copy(stopwatchRunning = true, stopwatchLastStart = System.currentTimeMillis())
            }
            taskUseCases.updateTask(updated)
            if (updated.stopwatchRunning) startStopwatchTicker(updated.id)
        }
    }

    private var stopwatchJob: Job? = null

    private fun startStopwatchTicker(taskId: Int) {
        stopwatchJob?.cancel()
        stopwatchJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val current = _taskList.value.find { it.id == taskId } ?: continue
                if (!current.stopwatchRunning) break
                val elapsed = current.stopwatchElapsed + (System.currentTimeMillis() - current.stopwatchLastStart)
                _taskList.value = _taskList.value.map {
                    if (it.id == taskId) it.copy(stopwatchElapsed = elapsed) else it
                }
            }
        }
    }

    fun resetStopwatch(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(stopwatchElapsed = 0L, stopwatchRunning = false, stopwatchLastStart = 0L)
            taskUseCases.updateTask(updated)
        }
    }

    fun toggleTaskDone(task: Task) {
        viewModelScope.launch {
            taskUseCases.updateTask(task.copy(isDone = !task.isDone))
        }
    }
}
