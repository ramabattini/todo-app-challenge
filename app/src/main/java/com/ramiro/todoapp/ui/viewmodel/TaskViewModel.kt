package com.ramiro.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.data.repository.SupabaseTaskRepository
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

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Errores de acciones puntuales (toggle, delete) sin tirar abajo la lista
    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

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
                _uiState.value = UiState.Error(e.message ?: "Error al cargar las tareas")
            }
        }
    }

    fun createTask(
        title: String,
        description: String,
        priority: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.createTask(title, description, priority)
                loadTasks()
                onSuccess()
            } catch (e: Exception) {
                onError()
                _actionError.value = e.message ?: "Error al crear la tarea"
            }
        }
    }

    fun updateTask(
        id: Long,
        title: String,
        description: String,
        priority: String,
        isCompleted: Boolean,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.updateTask(id, title, description, priority, isCompleted)
                loadTasks()
                onSuccess()
            } catch (e: Exception) {
                onError()
                _actionError.value = e.message ?: "Error al actualizar la tarea"
            }
        }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            try {
                repository.toggleCompleted(task.id, !task.isCompleted)
                loadTasks()
            } catch (e: Exception) {
                // Error puntual: no borramos la lista, mostramos snackbar
                _actionError.value = "No se pudo actualizar la tarea"
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteTask(id)
                loadTasks()
            } catch (e: Exception) {
                _actionError.value = "No se pudo eliminar la tarea"
            }
        }
    }

    fun clearActionError() {
        _actionError.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskViewModel(SupabaseTaskRepository()) as T
            }
        }
    }
}
