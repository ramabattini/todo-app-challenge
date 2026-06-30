package com.ramiro.todoapp

import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.data.repository.TaskRepository

class FakeTaskRepository(private val tasks: List<Task> = emptyList()) : TaskRepository {

    override suspend fun getTasks(): List<Task> = tasks

    override suspend fun createTask(title: String, description: String, priority: String): Task =
        Task(id = 99, title = title, description = description, priority = priority)

    override suspend fun updateTask(
        id: Long, title: String, description: String, priority: String, isCompleted: Boolean
    ): Task = tasks.find { it.id == id }!!.copy(title = title, isCompleted = isCompleted)

    override suspend fun toggleCompleted(id: Long, isCompleted: Boolean) {}

    override suspend fun deleteTask(id: Long) {}
}

class ErrorTaskRepository : TaskRepository {
    override suspend fun getTasks(): List<Task> = throw Exception("Sin conexion")
    override suspend fun createTask(title: String, description: String, priority: String): Task = throw Exception("Sin conexion")
    override suspend fun updateTask(id: Long, title: String, description: String, priority: String, isCompleted: Boolean): Task = throw Exception("Sin conexion")
    override suspend fun toggleCompleted(id: Long, isCompleted: Boolean) = throw Exception("Sin conexion")
    override suspend fun deleteTask(id: Long) = throw Exception("Sin conexion")
}
