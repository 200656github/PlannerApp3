package com.example.planner.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.planner.PlannerApplication
import com.example.planner.data.repository.TaskRepository
import com.example.planner.domain.model.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class DateCell(
    val date: LocalDate,
    val dayName: String,
    val dayNumber: Int,
    val isToday: Boolean,
    val isSelected: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository =
        (application as PlannerApplication).repository

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _weekDates = MutableStateFlow<List<DateCell>>(emptyList())
    val weekDates: StateFlow<List<DateCell>> = _weekDates.asStateFlow()

    val tasksForDate: StateFlow<List<Task>> = _selectedDate
        .flatMapLatest { date -> repository.getTasksByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { updateWeekDates() }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        updateWeekDates()
    }

    fun goToToday() { selectDate(LocalDate.now()) }

    fun goToPreviousWeek() {
        _selectedDate.value = _selectedDate.value.minusWeeks(1)
        updateWeekDates()
    }

    fun goToNextWeek() {
        _selectedDate.value = _selectedDate.value.plusWeeks(1)
        updateWeekDates()
    }

    private fun updateWeekDates() {
        val selected = _selectedDate.value
        val monday = selected.with(DayOfWeek.MONDAY)
        val today = LocalDate.now()
        _weekDates.value = (0..6).map { offset ->
            val date = monday.plusDays(offset.toLong())
            DateCell(
                date = date,
                dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                dayNumber = date.dayOfMonth,
                isToday = date == today,
                isSelected = date == selected
            )
        }
    }
}
