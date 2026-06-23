package com.dailytaskmanager.app.presentation.ui.task_list

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel
import com.dailytaskmanager.app.ui.theme.*
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
    var showSearch by remember { mutableStateOf(false) }

    val filters = listOf(
        "all" to "All", "today" to "Today", "overdue" to "Overdue",
        "meeting" to "Meetings", "project" to "Projects", "followUp" to "Follow-ups"
    )

    val doneCount = taskList.count { it.isDone }
    val totalCount = taskList.size
    val progress = if (totalCount > 0) doneCount.toFloat() / totalCount else 0f

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkSurface, DarkBackground),
                    startY = 0f,
                    endY = 2000f
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header Section ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DarkSurfaceVariant.copy(alpha = 0.6f),
                                DarkBackground.copy(alpha = 0f)
                            )
                        )
                    )
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = getGreeting(),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = TextSecondary,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.W300
                                )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
                                    .format(Date()).uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.W600,
                                    letterSpacing = 1.sp,
                                    fontSize = 20.sp
                                )
                            )
                        }

                        // ── Search toggle ──
                        IconButton(
                            onClick = { showSearch = !showSearch },
                            modifier = Modifier
                                .background(DarkSurfaceHigh, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                if (showSearch) Icons.Default.Close else Icons.Default.Search,
                                null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ── Stats row ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Progress ring
                        Box(
                            modifier = Modifier.size(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val sweepAngle by animateFloatAsState(
                                targetValue = progress * 360f,
                                animationSpec = tween(1000, easing = EaseOutCubic), label = ""
                            )
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeW = 5.dp.toPx()
                                val r = size.minDimension / 2 - strokeW / 2
                                val arcSize = androidx.compose.ui.geometry.Size(r * 2, r * 2)
                                drawCircle(
                                    color = DarkSurfaceHigh,
                                    style = Stroke(width = strokeW),
                                    center = center,
                                    radius = r + strokeW / 2
                                )
                                drawArc(
                                    color = Teal,
                                    startAngle = -90f,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    style = Stroke(width = strokeW, cap = StrokeCap.Round),
                                    topLeft = Offset(center.x - r, center.y - r),
                                    size = arcSize
                                )
                            }
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                color = Teal,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.W700)
                            )
                        }

                        Spacer(Modifier.width(20.dp))

                        Column {
                            Text(
                                text = "$totalCount tasks",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextPrimary,
                                    fontWeight = FontWeight.W600
                                )
                            )
                            Text(
                                text = "$doneCount completed",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary
                                )
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        FilledTonalButton(
                            onClick = onAddClick,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Teal.copy(alpha = 0.15f),
                                contentColor = Teal
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("New Task", fontWeight = FontWeight.W600)
                        }
                    }
                }
            }

            // ── Search Bar ──
            AnimatedVisibility(
                visible = showSearch,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    placeholder = { Text("Search tasks...", color = TextTertiary) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = TextTertiary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, null, tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = DarkSurfaceHigh,
                        cursorColor = Teal,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // ── Category filter chips ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { (key, label) ->
                    val selected = filterCategory == key
                    val chipColor = when (key) {
                        "overdue" -> Rose
                        "today" -> Sky
                        "meeting" -> Sky
                        "project" -> Violet
                        "followUp" -> Emerald
                        else -> TextSecondary
                    }
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.setFilter(key) },
                        label = {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.W600 else FontWeight.W400
                                )
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (selected) chipColor.copy(alpha = 0.15f) else DarkSurfaceHigh.copy(alpha = 0.5f),
                            labelColor = if (selected) chipColor else TextSecondary,
                            selectedContainerColor = chipColor.copy(alpha = 0.15f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selected) chipColor.copy(alpha = 0.3f) else Color.Transparent,
                            selectedBorderColor = chipColor.copy(alpha = 0.3f),
                            enabled = true,
                            selected = selected
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Task List ──
            if (taskList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inbox,
                            null,
                            Modifier.size(72.dp),
                            tint = TextTertiary.copy(alpha = 0.3f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No tasks yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.W500
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tap + to create your first task",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextTertiary)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(taskList, key = { it.id }) { task ->
                        TaskCard(
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
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    // ── Delete Dialog ──
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Delete Task") },
            text = { Text("Remove \"${taskToDelete!!.title}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(taskToDelete!!)
                    showDeleteDialog = false
                }) { Text("Delete", color = Rose) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

// ── Task Card ──
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleDone: () -> Unit,
    onToggleStopwatch: () -> Unit
) {
    val categoryInfo = mapOf(
        "meeting" to Triple("Meeting", CategoryMeeting, Icons.Default.Groups),
        "project" to Triple("Project", CategoryProject, Icons.Default.FolderOpen),
        "followUp" to Triple("Follow-up", CategoryFollowUp, Icons.Default.Send),
        "general" to Triple("General", CategoryGeneral, Icons.Default.TaskAlt)
    )
    val (catLabel, catColor, catIcon) = categoryInfo[task.category] ?: Triple("General", CategoryGeneral, Icons.Default.TaskAlt)

    val priorityInfo = mapOf(
        1 to Pair(PriorityLow, "Low"),
        2 to Pair(PriorityMedium, "Med"),
        3 to Pair(PriorityHigh, "High"),
        4 to Pair(PriorityUrgent, "Urg")
    )
    val (priColor, priLabel) = priorityInfo[task.priority] ?: Pair(PriorityLow, "Low")

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone) DarkSurfaceVariant.copy(alpha = 0.4f) else DarkSurface
        ),
        border = BorderStroke(
            1.dp,
            if (task.isDone) DarkSurfaceHigh.copy(alpha = 0.2f) else DarkSurfaceHigh.copy(alpha = 0.5f)
        )
    ) {
        Column {
            // Color accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        if (task.isDone) DarkSurfaceHigh
                        else catColor.copy(alpha = 0.8f)
                    )
            )

            Row(
                modifier = Modifier.padding(start = 4.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Checkbox
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (task.isDone) Teal.copy(alpha = 0.2f)
                            else DarkSurfaceHigh
                        )
                        .clickable { onToggleDone() },
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isDone) {
                        Icon(
                            Icons.Default.Check, null,
                            Modifier.size(16.dp), tint = Teal
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.W600,
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (task.isDone) TextSecondary else TextPrimary
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = priLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = priColor,
                                fontWeight = FontWeight.W700,
                                fontSize = 10.sp
                            )
                        )
                    }

                    if (task.description.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            maxLines = 1
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Category chip
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                catIcon, null,
                                Modifier.size(12.dp), tint = catColor
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                catLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = catColor,
                                    fontWeight = FontWeight.W500
                                )
                            )
                        }

                        // Due date
                        task.dueDate?.let { due ->
                            val isOverdue = due < System.currentTimeMillis() && !task.isDone
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Schedule, null,
                                    Modifier.size(12.dp),
                                    tint = if (isOverdue) Rose else TextTertiary
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(due)),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isOverdue) Rose else TextTertiary,
                                        fontWeight = if (isOverdue) FontWeight.W600 else FontWeight.W400
                                    )
                                )
                            }
                        }

                        // Assignee
                        if (task.assignee.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Person, null,
                                    Modifier.size(12.dp), tint = TextTertiary
                                )
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    task.assignee,
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextTertiary)
                                )
                            }
                        }

                        // Repeat indicator
                        if (task.repeatInterval != null) {
                            Icon(
                                Icons.Default.Repeat, null,
                                Modifier.size(12.dp), tint = TextTertiary
                            )
                        }

                        // Reminder indicator
                        if (task.reminderTime != null) {
                            Icon(
                                Icons.Default.Notifications, null,
                                Modifier.size(12.dp), tint = Amber
                            )
                        }
                    }
                }

                // Right side: stopwatch + delete
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Stopwatch
                    val elapsed = if (task.stopwatchRunning)
                        task.stopwatchElapsed + (System.currentTimeMillis() - task.stopwatchLastStart)
                    else task.stopwatchElapsed

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (task.stopwatchRunning) Teal.copy(alpha = 0.15f)
                                else DarkSurfaceHigh
                            )
                            .clickable { onToggleStopwatch() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.stopwatchRunning || elapsed > 0) {
                            Icon(
                                if (task.stopwatchRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                null, Modifier.size(16.dp),
                                tint = if (task.stopwatchRunning) Teal else TextSecondary
                            )
                        } else {
                            Icon(
                                Icons.Default.HourglassEmpty, null,
                                Modifier.size(16.dp), tint = TextTertiary
                            )
                        }
                    }

                    if (elapsed > 0) {
                        val secs = (elapsed / 1000) % 60
                        val mins = (elapsed / 60000) % 60
                        val hours = elapsed / 3600000
                        Text(
                            text = String.format("%d:%02d", hours, mins),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (task.stopwatchRunning) Teal else TextTertiary,
                                fontWeight = FontWeight.W600,
                                fontSize = 10.sp
                            )
                        )
                        if (secs > 0 || task.stopwatchRunning) {
                            Text(
                                text = String.format("%02d", secs),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (task.stopwatchRunning) Teal.copy(alpha = 0.7f) else TextTertiary.copy(alpha = 0.6f),
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }

                    // Delete
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceHigh.copy(alpha = 0.5f))
                            .clickable { onDeleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Delete, null,
                            Modifier.size(16.dp), tint = Rose.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..20 -> "Good evening"
        else -> "Good night"
    }
}
