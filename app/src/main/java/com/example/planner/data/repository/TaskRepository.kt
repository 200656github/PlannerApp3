package com.example.planner.data.repository

import com.example.planner.data.local.AppDatabase
import com.example.planner.domain.model.Task
import com.example.planner.domain.model.toDomain
import com.example.planner.domain.model.toEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val database: AppDatabase) {
    private val dao = database.taskDao()

    fun getAllTasks(): Flow<List<Task>> =
        dao.getAllTasks().map { entities -> entities.map { it.toDomain() } }

    fun getTasksByDate(date: LocalDate): Flow<List<Task>> =
        dao.getTasksByDate(date).map { entities -> entities.map { it.toDomain() } }

    fun getUnscheduledTasks(): Flow<List<Task>> =
        dao.getUnscheduledTasks().map { entities -> entities.map { it.toDomain() } }

    suspend fun getTaskById(id: Long): Task? =
        dao.getTaskById(id)?.toDomain()

    suspend fun insertTask(task: Task): Long =
        dao.insertTask(task.toEntity())

    suspend fun updateTask(task: Task) =
        dao.updateTask(task.toEntity())

    suspend fun deleteTask(task: Task) =
        dao.deleteTask(task.toEntity())

    suspend fun deleteTaskById(id: Long) =
        dao.deleteTaskById(id)
}
