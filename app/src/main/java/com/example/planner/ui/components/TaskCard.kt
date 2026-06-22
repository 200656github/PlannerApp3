package com.example.planner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.planner.domain.model.Priority
import com.example.planner.domain.model.Task
import com.example.planner.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> PriorityHigh
        Priority.MEDIUM -> PriorityMedium
        Priority.LOW -> PriorityLow
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp),
                color = priorityColor,
                shape = MaterialTheme.shapes.extraSmall
            ) {}

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(onClick = onToggleComplete) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Mark incomplete",
                        tint = CompletedGreen
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Circle,
                        contentDescription = "Mark complete",
                        tint = Gray300
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (task.isCompleted) Gray300 else Black,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (task.note.isNotBlank()) {
                            Text(
                                text = task.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray500,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    task.scheduledDate?.let { date ->
                        Spacer(modifier = Modifier.width(8.dp))
                        DateBadge(date)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateBadge(date: LocalDate) {
    val formatted = if (date.year == LocalDate.now().year) {
        date.format(DateTimeFormatter.ofPattern("MMM d"))
    } else {
        date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Rounded.Today,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Gray500
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = formatted,
            style = MaterialTheme.typography.bodySmall,
            color = Gray500
        )
    }
}
