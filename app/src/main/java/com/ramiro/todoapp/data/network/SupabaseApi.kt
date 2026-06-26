package com.ramiro.todoapp.data.network

import com.ramiro.todoapp.data.model.CreateTaskRequest
import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.data.model.UpdateTaskRequest
import retrofit2.http.*

interface SupabaseApi {

    @GET("rest/v1/tasks")
    suspend fun getTasks(
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<Task>

    @POST("rest/v1/tasks")
    suspend fun createTask(@Body task: CreateTaskRequest): List<Task>

    @PATCH("rest/v1/tasks")
    suspend fun updateTask(
        @Query("id") idFilter: String,
        @Body task: UpdateTaskRequest
    ): List<Task>

    @DELETE("rest/v1/tasks")
    suspend fun deleteTask(@Query("id") idFilter: String)
}
