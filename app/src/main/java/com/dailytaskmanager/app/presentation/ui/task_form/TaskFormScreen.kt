package com.dailytaskmanager.app.presentation.ui.task_form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel

@Composable
fun TaskFormScreen(
    viewModel: TaskViewModel,
    taskId: Int?,
    onBack: () -> Unit
) {
    val selectedTask by viewModel.selectedTask.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(taskId) {
        taskId?.let {
            viewModel.loadTaskById(it)
        }
    }

    LaunchedEffect(selectedTask) {
        selectedTask?.let {
            title = it.title
            description = it.description
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val task = Task(
                id = taskId ?: 0,
                title = title,
                description = description,
                timestamp = System.currentTimeMillis()
            )
            if (taskId == null) {
                viewModel.addTask(task)
            } else {
                viewModel.updateTask(task)
            }
            onBack()
        }) {
            Text(if (taskId == null) "Add Task" else "Update Task")
        }
    }
}
