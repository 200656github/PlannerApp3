package com.example.planner.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planner.domain.model.Priority
import com.example.planner.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Long,
    onBack: () -> Unit,
    viewModel: TaskEditViewModel = viewModel()
) {
    LaunchedEffect(taskId) { viewModel.loadTask(taskId) }

    val title by viewModel.title.collectAsState()
    val note by viewModel.note.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val scheduledDate by viewModel.scheduledDate.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (taskId > 0) "Edit Task" else "New Task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (taskId > 0) {
                        IconButton(onClick = { viewModel.delete(onBack) }) {
                            Icon(
                                Icons.Rounded.Delete,
                                contentDescription = "Delete",
                                tint = PriorityHigh
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = { viewModel.save(onBack) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    enabled = title.isNotBlank() && !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Black,
                        contentColor = White
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(if (taskId > 0) "Save" else "Add Task")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::updateTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Task title", color = Gray300) },
                textStyle = MaterialTheme.typography.titleMedium,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gray500,
                    unfocusedBorderColor = Gray100,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                )
            )

            // Note input
            OutlinedTextField(
                value = note,
                onValueChange = viewModel::updateNote,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Notes (optional)", color = Gray300) },
                textStyle = MaterialTheme.typography.bodyMedium,
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gray500,
                    unfocusedBorderColor = Gray100,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White
                )
            )

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = Gray100,
                thickness = 1.dp
            )

            // Priority selector
            Text(
                text = "Priority",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->
                    val isSelected = priority == p
                    val color = when (p) {
                        Priority.LOW -> PriorityLow
                        Priority.MEDIUM -> PriorityMedium
                        Priority.HIGH -> PriorityHigh
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.updatePriority(p) },
                        label = {
                            Text(
                                text = p.name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.15f),
                            selectedLabelColor = color
                        )
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = Gray100,
                thickness = 1.dp
            )

            // Date picker trigger
            Text(
                text = "Schedule",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            TextButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    Icons.Rounded.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Gray700
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = scheduledDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        ?: "No date set",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (scheduledDate != null) Black else Gray500
                )
            }
            if (scheduledDate != null) {
                TextButton(
                    onClick = { viewModel.updateScheduledDate(null) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text("Clear date", style = MaterialTheme.typography.bodySmall, color = PriorityHigh)
                }
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = scheduledDate
                ?.atStartOfDay(java.time.ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        viewModel.updateScheduledDate(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
