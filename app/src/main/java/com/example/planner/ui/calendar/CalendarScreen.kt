package com.example.planner.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.planner.domain.model.Task
import com.example.planner.ui.components.TaskCard
import com.example.planner.ui.theme.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onTaskClick: (Task) -> Unit = {},
    viewModel: CalendarViewModel = viewModel()
) {
    val weekDates by viewModel.weekDates.collectAsState()
    val tasksForDate by viewModel.tasksForDate.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedDate.format(
                                DateTimeFormatter.ofPattern("MMMM yyyy")
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = { viewModel.goToToday() }) {
                            Text("Today", style = MaterialTheme.typography.bodySmall, color = Gray500)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.goToPreviousWeek() }) {
                        Icon(Icons.Rounded.ChevronLeft, contentDescription = "Previous week")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.goToNextWeek() }) {
                        Icon(Icons.Rounded.ChevronRight, contentDescription = "Next week")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(weekDates) { cell ->
                    DateItem(cell = cell, onClick = { viewModel.selectDate(cell.date) })
                }
            }

            Divider(color = Gray100, thickness = 1.dp)

            if (tasksForDate.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks for this day",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray500
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasksForDate, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = {},
                            onClick = { onTaskClick(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateItem(cell: DateCell, onClick: () -> Unit) {
    val bgColor = when {
        cell.isSelected -> Black
        cell.isToday -> Gray100
        else -> White
    }
    val textColor = when {
        cell.isSelected -> White
        cell.isToday -> Black
        else -> Gray700
    }

    Column(
        modifier = Modifier
            .width(44.dp)
            .clip(MaterialTheme.shapes.small)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cell.dayName,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = if (cell.isSelected) White.copy(alpha = 0.7f) else Gray500
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = cell.dayNumber.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (cell.isToday || cell.isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = textColor
        )
    }
}
