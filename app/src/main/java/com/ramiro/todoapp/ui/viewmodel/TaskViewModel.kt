package com.ramiro.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val tasks: List<Task>) : UiState()
    data class Error(val message: String) : UiState()
}

class TaskViewModel : ViewModel() {
    private val repository = TaskRepository()

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val tasks = repository.getTasks()
                _uiState.value = UiState.Success(tasks)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createTask(title: String, description: String, priority: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.createTask(title, description, priority)
                loadTasks()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al crear tarea")
            }
        }
    }

    fun updateTask(
        id: Long,
        title: String,
        description: String,
        priority: String,
        isCompleted: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateTask(id, title, description, priority, isCompleted)
                loadTasks()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al actualizar tarea")
            }
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            try {
                repository.toggleCompleted(task.id, !task.isCompleted)
                loadTasks()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteTask(id)
                loadTasks()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }
}
