package com.example.planner.ui.tasklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.PlannerApplication
import com.example.planner.data.repository.TaskRepository
import com.example.planner.domain.model.Task
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortMode(val label: String) {
    PRIORITY("Priority"),
    DATE("Date"),
    CREATED("Created")
}

data class TaskGroups(
    val active: List<Task>,
    val completed: List<Task>
)

class TaskListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository =
        (application as PlannerApplication).repository

    private val _sortMode = MutableStateFlow(SortMode.PRIORITY)
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

    private val allTasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tasks: StateFlow<List<Task>> = combine(allTasks, _sortMode) { tasks, mode ->
        when (mode) {
            SortMode.PRIORITY -> tasks.sortedWith(
                compareByDescending<Task> { it.priority.value }
                    .thenBy { it.createdAt }
            )
            SortMode.DATE -> tasks.sortedWith(
                compareBy<Task> { it.scheduledDate ?: java.time.LocalDate.MAX }
                    .thenByDescending { it.priority.value }
            )
            SortMode.CREATED -> tasks.sortedByDescending { it.createdAt }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val taskGroups: StateFlow<TaskGroups> = tasks.map { list ->
        TaskGroups(
            active = list.filter { !it.isCompleted },
            completed = list.filter { it.isCompleted }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskGroups(emptyList(), emptyList())
    )

    fun setSortMode(mode: SortMode) { _sortMode.value = mode }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
