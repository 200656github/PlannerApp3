package com.example.planner.domain.model

import com.example.planner.data.local.entity.TaskEntity
import java.time.LocalDate

enum class Priority(val value: Int) {
    LOW(0), MEDIUM(1), HIGH(2);

    companion object {
        fun fromInt(value: Int): Priority =
            entries.firstOrNull { it.value == value } ?: MEDIUM
    }
}

data class Task(
    val id: Long = 0,
    val title: String,
    val note: String = "",
    val priority: Priority = Priority.MEDIUM,
    val scheduledDate: LocalDate? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    note = note,
    priority = Priority.fromInt(priority),
    scheduledDate = scheduledDate,
    isCompleted = isCompleted,
    createdAt = createdAt
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    note = note,
    priority = priority.value,
    scheduledDate = scheduledDate,
    isCompleted = isCompleted,
    createdAt = createdAt
)
