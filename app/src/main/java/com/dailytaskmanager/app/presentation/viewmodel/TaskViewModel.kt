package com.dailytaskmanager.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.usecase.TaskUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    val taskList: StateFlow<List<Task>> = _taskList.asStateFlow()

    private val _selectedTask = MutableStateFlow<Task?>(null)
    val selectedTask: StateFlow<Task?> = _selectedTask.asStateFlow()

    init {
        getAllTasks()
    }

    private fun getAllTasks() {
        viewModelScope.launch {
            taskUseCases.getAllTasks()
                .collect { tasks ->
                    _taskList.value = tasks
                }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskUseCases.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskUseCases.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskUseCases.deleteTask(task)
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
}
