package com.dailytaskmanager.app.presentation.ui.task_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onAddClick: () -> Unit,
    onTaskClick: (Int) -> Unit
) {
    val taskList by viewModel.taskList.collectAsState()
    val filterCategory by viewModel.filterCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    val filters = listOf(
        "all" to "All",
        "today" to "Today",
        "overdue" to "Overdue",
        "meeting" to "Meetings",
        "project" to "Projects",
        "followUp" to "Follow-ups"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Tasks") },
                actions = {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (searchQuery.isNotEmpty()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search tasks...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    singleLine = true
                )
            }

            ScrollableTabRow(
                selectedTabIndex = filters.indexOfFirst { it.first == filterCategory }.coerceAtLeast(0),
                edgePadding = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                filters.forEach { (key, label) ->
                    Tab(
                        selected = filterCategory == key,
                        onClick = { viewModel.setFilter(key) },
                        text = { Text(label) }
                    )
                }
            }

            if (taskList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircleOutline, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Spacer(Modifier.height(8.dp))
                        Text("No tasks found", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(taskList, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onClick = { onTaskClick(task.id) },
                            onDeleteClick = {
                                taskToDelete = task
                                showDeleteDialog = true
                            },
                            onToggleDone = { viewModel.toggleTaskDone(task) },
                            onToggleStopwatch = { viewModel.toggleStopwatch(task) }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog && taskToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Task") },
                text = { Text("Delete \"${taskToDelete!!.title}\"?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTask(taskToDelete!!)
                        showDeleteDialog = false
                    }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleDone: () -> Unit,
    onToggleStopwatch: () -> Unit
) {
    val categoryColors = mapOf(
        "meeting" to MaterialTheme.colorScheme.tertiary,
        "project" to MaterialTheme.colorScheme.primary,
        "followUp" to MaterialTheme.colorScheme.secondary,
        "general" to MaterialTheme.colorScheme.outline
    )
    val categoryLabels = mapOf(
        "meeting" to "Meeting",
        "project" to "Project",
        "followUp" to "Follow-up",
        "general" to "General"
    )
    val priorityColors = mapOf(
        1 to Color(0xFF4CAF50),
        2 to Color(0xFFFF9800),
        3 to Color(0xFFF44336),
        4 to Color(0xFFD32F2F)
    )
    val priorityLabels = mapOf(1 to "!", 2 to "!!", 3 to "!!!", 4 to "!!!!")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { onToggleDone() }
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = priorityLabels[task.priority] ?: "",
                    color = priorityColors[task.priority] ?: Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(categoryLabels[task.category] ?: "General", style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = (categoryColors[task.category] ?: Color.Gray).copy(alpha = 0.15f)
                    )
                )

                task.dueDate?.let { due ->
                    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                    val color = if (due < System.currentTimeMillis()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    Text(
                        text = "Due: ${sdf.format(Date(due))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = color
                    )
                }

                if (task.assignee.isNotBlank()) {
                    Icon(Icons.Default.Person, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(task.assignee, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.weight(1f))

                if (task.stopwatchRunning || task.stopwatchElapsed > 0) {
                    val elapsed = if (task.stopwatchRunning)
                        task.stopwatchElapsed + (System.currentTimeMillis() - task.stopwatchLastStart)
                    else task.stopwatchElapsed
                    val secs = (elapsed / 1000) % 60
                    val mins = (elapsed / 60000) % 60
                    val hours = elapsed / 3600000
                    IconButton(onClick = onToggleStopwatch, modifier = Modifier.size(24.dp)) {
                        Icon(
                            if (task.stopwatchRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            if (task.stopwatchRunning) "Pause" else "Start",
                            Modifier.size(18.dp),
                            tint = if (task.stopwatchRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = String.format("%d:%02d:%02d", hours, mins, secs),
                        style = MaterialTheme.typography.labelSmall
                    )
                } else {
                    IconButton(onClick = onToggleStopwatch, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.AccessTime, "Start timer", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, "Delete", Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
