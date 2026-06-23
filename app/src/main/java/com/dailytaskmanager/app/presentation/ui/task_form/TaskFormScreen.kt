package com.dailytaskmanager.app.presentation.ui.task_form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    viewModel: TaskViewModel,
    taskId: Int?,
    onBack: () -> Unit
) {
    val selectedTask by viewModel.selectedTask.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }
    var assignee by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("general") }
    var priority by remember { mutableIntStateOf(2) }
    var hasReminder by remember { mutableStateOf(false) }
    var reminderDate by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("") }
    var repeatReminder by remember { mutableStateOf(false) }
    var repeatInterval by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categories = listOf("general" to "General", "meeting" to "Meeting", "project" to "Project", "followUp" to "Follow Up")

    LaunchedEffect(taskId) {
        taskId?.let { viewModel.loadTaskById(it) }
    }

    LaunchedEffect(selectedTask) {
        selectedTask?.let { task ->
            title = task.title
            description = task.description
            assignee = task.assignee
            category = task.category
            priority = task.priority
            task.dueDate?.let { d ->
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = d }
                dueDate = String.format("%04d-%02d-%02d", get(1), get(2) + 1, get(5))
                dueTime = String.format("%02d:%02d", get(11), get(12))
            }
            task.reminderTime?.let { r ->
                hasReminder = true
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = r }
                reminderDate = String.format("%04d-%02d-%02d", get(1), get(2) + 1, get(5))
                reminderTime = String.format("%02d:%02d", get(11), get(12))
            }
            repeatReminder = task.repeatInterval != null
            task.repeatInterval?.let {
                repeatInterval = (it / 60000).toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId == null) "New Task" else "Edit Task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(12.dp))

            Text("Category", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = categories.find { it.first == category }?.second ?: "General",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { category = key; categoryExpanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Priority", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth()) {
                listOf(1 to "Low", 2 to "Medium", 3 to "High", 4 to "Urgent").forEach { (p, label) ->
                    FilterChip(
                        selected = priority == p,
                        onClick = { priority = p },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = assignee,
                onValueChange = { assignee = it },
                label = { Text("Assigned To") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter name or email") }
            )

            Spacer(Modifier.height(12.dp))

            Text("Due Date & Time", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = dueTime,
                    onValueChange = { dueTime = it },
                    label = { Text("Time") },
                    placeholder = { Text("HH:MM") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            Text("Reminder", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = hasReminder, onCheckedChange = { hasReminder = it })
                Text("Set reminder")
            }

            if (hasReminder) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = reminderDate,
                        onValueChange = { reminderDate = it },
                        label = { Text("Reminder Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = reminderTime,
                        onValueChange = { reminderTime = it },
                        label = { Text("Reminder Time") },
                        placeholder = { Text("HH:MM") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(checked = repeatReminder, onCheckedChange = { repeatReminder = it })
                    Text("Repeat every")
                }

                if (repeatReminder) {
                    OutlinedTextField(
                        value = repeatInterval,
                        onValueChange = { repeatInterval = it },
                        label = { Text("Repeat interval (minutes)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. 60") }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val dueTimestamp = parseDateTime(dueDate, dueTime)
                    val reminderTimestamp = if (hasReminder) parseDateTime(reminderDate, reminderTime) else null
                    val interval = if (repeatReminder) (repeatInterval.toLongOrNull() ?: 0) * 60000 else null

                    val task = Task(
                        id = taskId ?: 0,
                        title = title,
                        description = description,
                        timestamp = System.currentTimeMillis(),
                        dueDate = dueTimestamp,
                        assignee = assignee,
                        priority = priority,
                        category = category,
                        reminderTime = reminderTimestamp,
                        repeatInterval = interval,
                        isDone = selectedTask?.isDone ?: false
                    )
                    if (taskId == null) viewModel.addTask(task) else viewModel.updateTask(task)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text(if (taskId == null) "Add Task" else "Update Task")
            }
        }
    }
}

private fun parseDateTime(date: String, time: String): Long? {
    if (date.isBlank()) return null
    val parts = date.split("-")
    if (parts.size != 3) return null
    val tParts = if (time.isNotBlank()) time.split(":") else listOf("0", "0")
    return try {
        val cal = java.util.Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt(), tParts[0].toInt(), tParts[1].toInt(), 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        cal.timeInMillis
    } catch (_: Exception) { null }
}
