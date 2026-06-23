package com.dailytaskmanager.app.presentation.ui.task_form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel
import com.dailytaskmanager.app.ui.theme.*
import com.dailytaskmanager.app.presentation.ui.task_form.DateTimeUtil

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

    val catKeys = listOf("general", "meeting", "project", "followUp")
    val catLabels = listOf("General", "Meeting", "Project", "Follow Up")
    val catColors = listOf(CategoryGeneral, CategoryMeeting, CategoryProject, CategoryFollowUp)

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
            task.repeatInterval?.let { repeatInterval = (it / 60000).toString() }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkBackground)
                )
            )
    ) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (taskId == null) "New Task" else "Edit Task",
                            fontWeight = FontWeight.W600,
                            color = TextPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, null, tint = TextSecondary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Title ──
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task title") },
                    placeholder = { Text("What needs to be done?", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(12.dp))

                // ── Description ──
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Add details...", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                // ── Category ──
                Text("Category", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary, fontWeight = FontWeight.W600, letterSpacing = 0.5.sp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    catKeys.forEachIndexed { i, key ->
                        val selected = category == key
                        val color = catColors[i]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selected) color.copy(alpha = 0.15f)
                                    else DarkSurfaceHigh.copy(alpha = 0.5f)
                                )
                                .border(
                                    1.dp,
                                    if (selected) color.copy(alpha = 0.4f) else androidx.compose.ui.graphics.Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { category = key }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                catLabels[i],
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (selected) color else TextSecondary,
                                    fontWeight = if (selected) FontWeight.W600 else FontWeight.W400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Priority ──
                Text("Priority", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary, fontWeight = FontWeight.W600, letterSpacing = 0.5.sp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val prioValues = listOf(1, 2, 3, 4)
                    val prioLabels = listOf("Low", "Medium", "High", "Urgent")
                    val prioColors = listOf(PriorityLow, PriorityMedium, PriorityHigh, PriorityUrgent)
                    prioValues.forEachIndexed { i, p ->
                        val selected = priority == p
                        val color = prioColors[i]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (selected) color.copy(alpha = 0.15f)
                                    else DarkSurfaceHigh.copy(alpha = 0.5f)
                                )
                                .border(
                                    1.dp,
                                    if (selected) color.copy(alpha = 0.4f) else androidx.compose.ui.graphics.Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { priority = p }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                prioLabels[i],
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (selected) color else TextSecondary,
                                    fontWeight = if (selected) FontWeight.W700 else FontWeight.W400
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Assignee ──
                Text("Assignee", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary, fontWeight = FontWeight.W600, letterSpacing = 0.5.sp))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = assignee,
                    onValueChange = { assignee = it },
                    placeholder = { Text("Name or email", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = TextTertiary, modifier = Modifier.size(18.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(20.dp))

                // ── Due Date ──
                Text("Due Date & Time", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary, fontWeight = FontWeight.W600, letterSpacing = 0.5.sp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        placeholder = { Text("YYYY-MM-DD", color = TextTertiary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = TextTertiary, modifier = Modifier.size(18.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        placeholder = { Text("HH:MM", color = TextTertiary) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Schedule, null, tint = TextTertiary, modifier = Modifier.size(18.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ── Reminder Section ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, null, Modifier.size(20.dp), tint = Amber)
                                Spacer(Modifier.width(8.dp))
                                Text("Reminder", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary, fontWeight = FontWeight.W600))
                            }
                            Switch(
                                checked = hasReminder,
                                onCheckedChange = { hasReminder = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Teal,
                                    checkedTrackColor = Teal.copy(alpha = 0.3f),
                                    uncheckedThumbColor = TextTertiary,
                                    uncheckedTrackColor = DarkSurfaceHigh
                                )
                            )
                        }

                        AnimatedVisibility(visible = hasReminder) {
                            Column {
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = reminderDate,
                                        onValueChange = { reminderDate = it },
                                        placeholder = { Text("YYYY-MM-DD", color = TextTertiary) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    OutlinedTextField(
                                        value = reminderTime,
                                        onValueChange = { reminderTime = it },
                                        placeholder = { Text("HH:MM", color = TextTertiary) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }

                                Spacer(Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Repeat, null, Modifier.size(16.dp), tint = TextSecondary)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Repeat", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Switch(
                                        checked = repeatReminder,
                                        onCheckedChange = { repeatReminder = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Teal,
                                            checkedTrackColor = Teal.copy(alpha = 0.3f),
                                            uncheckedThumbColor = TextTertiary,
                                            uncheckedTrackColor = DarkSurfaceHigh
                                        )
                                    )
                                }

                                if (repeatReminder) {
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = repeatInterval,
                                        onValueChange = { repeatInterval = it },
                                        placeholder = { Text("e.g. 60", color = TextTertiary) },
                                        label = { Text("Interval (minutes)", color = TextSecondary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ── Submit Button ──
                Button(
                    onClick = {
                        val dueTimestamp = DateTimeUtil.parseDateTime(dueDate, dueTime)
                        val reminderTimestamp = if (hasReminder) DateTimeUtil.parseDateTime(reminderDate, reminderTime) else null
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = title.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal,
                        contentColor = DarkBackground,
                        disabledContainerColor = DarkSurfaceHigh
                    )
                ) {
                    Icon(
                        if (taskId == null) Icons.Default.Add else Icons.Default.Save,
                        null, Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (taskId == null) "Create Task" else "Update Task",
                        fontWeight = FontWeight.W700,
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

// DateTimeUtil.parseDateTime helper is in DateTimeUtil.kt
