package com.ramiro.todoapp.data.repository

import com.ramiro.todoapp.data.model.CreateTaskRequest
import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.data.model.UpdateTaskRequest
import com.ramiro.todoapp.data.remote.RetrofitClient

class TaskRepository {
    private val api = RetrofitClient.api

    suspend fun getTasks(): List<Task> = api.getTasks()

    suspend fun createTask(title: String, description: String, priority: String): Task =
        api.createTask(CreateTaskRequest(title, description, priority)).first()

    suspend fun updateTask(
        id: Long,
        title: String,
        description: String,
        priority: String,
        isCompleted: Boolean
    ): Task = api.updateTask(
        idFilter = "eq.$id",
        task = UpdateTaskRequest(title, description, priority, isCompleted)
    ).first()

    suspend fun toggleCompleted(id: Long, isCompleted: Boolean) {
        api.updateTask(
            idFilter = "eq.$id",
            task = UpdateTaskRequest(isCompleted = isCompleted)
        )
    }

    suspend fun deleteTask(id: Long) {
        api.deleteTask("eq.$id")
    }
}
