package com.example.planner

import android.app.Application
import com.example.planner.data.local.AppDatabase
import com.example.planner.data.repository.TaskRepository

class PlannerApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database) }
}
