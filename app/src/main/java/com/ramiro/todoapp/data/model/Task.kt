package com.ramiro.todoapp.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val priority: String = "medium",
    @SerializedName("is_completed")
    val isCompleted: Boolean = false,
    @SerializedName("created_at")
    val createdAt: String = ""
)

data class CreateTaskRequest(
    val title: String,
    val description: String,
    val priority: String
)

data class UpdateTaskRequest(
    val title: String? = null,
    val description: String? = null,
    val priority: String? = null,
    @SerializedName("is_completed")
    val isCompleted: Boolean? = null
)
