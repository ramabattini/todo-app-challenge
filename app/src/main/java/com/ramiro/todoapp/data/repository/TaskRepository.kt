package com.ramiro.todoapp.data.repository

import com.ramiro.todoapp.data.model.Task

interface TaskRepository {
    suspend fun getTasks(): List<Task>
    suspend fun createTask(title: String, description: String, priority: String): Task
    suspend fun updateTask(id: Long, title: String, description: String, priority: String, isCompleted: Boolean): Task
    suspend fun toggleCompleted(id: Long, isCompleted: Boolean)
    suspend fun deleteTask(id: Long)
}
