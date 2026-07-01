package com.ramiro.todoapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramiro.todoapp.data.model.Priority
import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.ui.viewmodel.TaskViewModel
import com.ramiro.todoapp.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    viewModel: TaskViewModel,
    onEditClick: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val task = (uiState as? UiState.Success)?.tasks?.find { it.id == taskId }
    val taskNotFound = uiState is UiState.Success && task == null
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de tarea") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (task != null) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFE53935))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when {
            taskNotFound -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Tarea no encontrada", style = MaterialTheme.typography.titleMedium)
                        Button(onClick = onBack) { Text("Volver") }
                    }
                }
            }
            task == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                TaskDetailContent(task = task, modifier = Modifier.padding(padding))
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar tarea") },
                text = { Text("¿Estás seguro que querés eliminar esta tarea?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteTask(taskId, onSuccess = onBack)
                    }) {
                        Text("Eliminar", color = Color(0xFFE53935))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun TaskDetailContent(task: Task, modifier: Modifier = Modifier) {
    val priority = Priority.fromValue(task.priority)
    val priorityColor = when (priority) {
        Priority.HIGH -> Color(0xFFE53935)
        Priority.MEDIUM -> Color(0xFFFB8C00)
        Priority.LOW -> Color(0xFF43A047)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = task.title, style = MaterialTheme.typography.headlineMedium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(color = priorityColor.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
                Text(
                    text = "Prioridad: ${priority.label}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = priorityColor
                )
            }
            Surface(
                color = if (task.isCompleted) Color(0xFF43A047).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (task.isCompleted) "Completada" else "Pendiente",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (task.isCompleted) Color(0xFF43A047) else Color.Gray
                )
            }
        }

        HorizontalDivider()

        Text(text = "Descripción", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
        Text(
            text = if (task.description.isNotEmpty()) task.description else "Sin descripción",
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.description.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onSurface
        )
    }
}
