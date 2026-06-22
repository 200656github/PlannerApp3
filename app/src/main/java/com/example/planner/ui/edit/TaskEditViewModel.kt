package com.example.planner.ui.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.PlannerApplication
import com.example.planner.data.repository.TaskRepository
import com.example.planner.domain.model.Priority
import com.example.planner.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskEditViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository =
        (application as PlannerApplication).repository

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    private val _priority = MutableStateFlow(Priority.MEDIUM)
    val priority: StateFlow<Priority> = _priority.asStateFlow()

    private val _scheduledDate = MutableStateFlow<LocalDate?>(null)
    val scheduledDate: StateFlow<LocalDate?> = _scheduledDate.asStateFlow()

    private var existingTask: Task? = null

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    fun loadTask(taskId: Long) {
        if (taskId <= 0) return
        viewModelScope.launch {
            repository.getTaskById(taskId)?.let { task ->
                existingTask = task
                _title.value = task.title
                _note.value = task.note
                _priority.value = task.priority
                _scheduledDate.value = task.scheduledDate
            }
        }
    }

    fun updateTitle(value: String) { _title.value = value }
    fun updateNote(value: String) { _note.value = value }
    fun updatePriority(value: Priority) { _priority.value = value }
    fun updateScheduledDate(value: LocalDate?) { _scheduledDate.value = value }

    fun save(onSaved: () -> Unit) {
        if (_title.value.isBlank()) return
        viewModelScope.launch {
            _isSaving.value = true
            val task = Task(
                id = existingTask?.id ?: 0,
                title = _title.value.trim(),
                note = _note.value.trim(),
                priority = _priority.value,
                scheduledDate = _scheduledDate.value,
                isCompleted = existingTask?.isCompleted ?: false,
                createdAt = existingTask?.createdAt ?: System.currentTimeMillis()
            )
            if (existingTask != null) {
                repository.updateTask(task)
            } else {
                repository.insertTask(task)
            }
            _isSaving.value = false
            onSaved()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        val task = existingTask ?: return
        viewModelScope.launch {
            repository.deleteTask(task)
            onDeleted()
        }
    }
}
