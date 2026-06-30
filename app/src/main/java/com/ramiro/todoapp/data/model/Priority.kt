package com.ramiro.todoapp.data.model

enum class Priority(val value: String, val label: String) {
    HIGH("high", "Alta"),
    MEDIUM("medium", "Media"),
    LOW("low", "Baja");

    companion object {
        fun fromValue(value: String): Priority =
            entries.find { it.value == value } ?: MEDIUM
    }
}
