package com.ramiro.todoapp

import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.ui.viewmodel.TaskViewModel
import com.ramiro.todoapp.ui.viewmodel.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loadTasks expone Success con las tareas del repositorio`() = runTest {
        val tareas = listOf(
            Task(id = 1, title = "Comprar leche", priority = "low"),
            Task(id = 2, title = "Estudiar Kotlin", priority = "high")
        )
        val vm = TaskViewModel(FakeTaskRepository(tareas))
        advanceUntilIdle()

        val estado = vm.uiState.value
        assertTrue(estado is UiState.Success)
        assertEquals(2, (estado as UiState.Success).tasks.size)
        assertEquals("Comprar leche", estado.tasks[0].title)
    }

    @Test
    fun `loadTasks expone Error cuando el repositorio falla`() = runTest {
        val vm = TaskViewModel(ErrorTaskRepository())
        advanceUntilIdle()

        val estado = vm.uiState.value
        assertTrue(estado is UiState.Error)
        assertEquals("Sin conexion", (estado as UiState.Error).message)
    }

    @Test
    fun `createTask llama onSuccess cuando el repositorio responde bien`() = runTest {
        val vm = TaskViewModel(FakeTaskRepository())
        var exitoLlamado = false

        vm.createTask(
            title = "Nueva tarea",
            description = "Descripcion",
            priority = "medium",
            onSuccess = { exitoLlamado = true },
            onError = { fail("No deberia llamar onError") }
        )
        advanceUntilIdle()

        assertTrue(exitoLlamado)
    }

    @Test
    fun `createTask llama onError cuando el repositorio falla`() = runTest {
        val vm = TaskViewModel(ErrorTaskRepository())
        // cargamos con fake para que no quede en error de lista
        var errorLlamado = false

        vm.createTask(
            title = "Nueva tarea",
            description = "",
            priority = "medium",
            onSuccess = { fail("No deberia llamar onSuccess") },
            onError = { errorLlamado = true }
        )
        advanceUntilIdle()

        assertTrue(errorLlamado)
    }

    @Test
    fun `toggleCompleted expone actionError sin borrar la lista cuando falla`() = runTest {
        val tareas = listOf(Task(id = 1, title = "Tarea A"))
        // Repositorio que falla solo en toggle
        val fakeConError = object : FakeTaskRepository(tareas) {
            override suspend fun toggleCompleted(id: Long, isCompleted: Boolean) =
                throw Exception("Sin conexion")
        }
        val vm = TaskViewModel(fakeConError)
        advanceUntilIdle()

        vm.toggleCompleted(tareas[0])
        advanceUntilIdle()

        // La lista sigue visible (no se reemplaza con Error)
        assertTrue(vm.uiState.value is UiState.Success)
        assertNotNull(vm.actionError.value)
    }

    @Test
    fun `clearActionError limpia el error de accion`() = runTest {
        val vm = TaskViewModel(ErrorTaskRepository())
        advanceUntilIdle()

        vm.clearActionError()

        assertNull(vm.actionError.value)
    }
}
