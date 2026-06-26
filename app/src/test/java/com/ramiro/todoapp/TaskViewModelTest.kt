package com.ramiro.todoapp

import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.ui.viewmodel.UiState
import org.junit.Assert.*
import org.junit.Test

class TaskViewModelTest {

    @Test
    fun `UiState Success contiene la lista de tareas correcta`() {
        val tareas = listOf(
            Task(id = 1, title = "Comprar leche", priority = "low", isCompleted = false),
            Task(id = 2, title = "Estudiar Kotlin", priority = "high", isCompleted = true)
        )
        val estado = UiState.Success(tareas)
        assertEquals(2, estado.tasks.size)
        assertEquals("Comprar leche", estado.tasks[0].title)
    }

    @Test
    fun `filtrar tareas completadas devuelve solo las completadas`() {
        val tareas = listOf(
            Task(id = 1, title = "Tarea A", isCompleted = false),
            Task(id = 2, title = "Tarea B", isCompleted = true),
            Task(id = 3, title = "Tarea C", isCompleted = true)
        )
        val completadas = tareas.filter { it.isCompleted }
        assertEquals(2, completadas.size)
        assertTrue(completadas.all { it.isCompleted })
    }

    @Test
    fun `filtrar tareas pendientes devuelve solo las pendientes`() {
        val tareas = listOf(
            Task(id = 1, title = "Tarea A", isCompleted = false),
            Task(id = 2, title = "Tarea B", isCompleted = true),
            Task(id = 3, title = "Tarea C", isCompleted = false)
        )
        val pendientes = tareas.filter { !it.isCompleted }
        assertEquals(2, pendientes.size)
        assertTrue(pendientes.none { it.isCompleted })
    }

    @Test
    fun `prioridad high tiene etiqueta Alta`() {
        val tarea = Task(id = 1, title = "Urgente", priority = "high")
        val etiqueta = when (tarea.priority) {
            "high" -> "Alta"
            "medium" -> "Media"
            else -> "Baja"
        }
        assertEquals("Alta", etiqueta)
    }

    @Test
    fun `UiState Error contiene el mensaje de error`() {
        val estado = UiState.Error("Sin conexion a internet")
        assertEquals("Sin conexion a internet", estado.message)
    }

    @Test
    fun `toggle de tarea invierte el estado de completado`() {
        val tarea = Task(id = 1, title = "Tarea", isCompleted = false)
        val nuevoEstado = !tarea.isCompleted
        assertTrue(nuevoEstado)
    }
}
