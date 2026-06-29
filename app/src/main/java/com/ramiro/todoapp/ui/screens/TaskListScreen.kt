package com.ramiro.todoapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ramiro.todoapp.data.model.Task
import com.ramiro.todoapp.ui.viewmodel.TaskViewModel
import com.ramiro.todoapp.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onTaskClick: (Long) -> Unit,
    onAddClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("all") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Nueva tarea")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Error: ${state.message}")
                            Button(onClick = { viewModel.loadTasks() }) { Text("Reintentar") }
                        }
                    }
                }
                is UiState.Success -> {
                    val total = state.tasks.size
                    val completed = state.tasks.count { it.isCompleted }
                    val pending = total - completed

                    // Tarjeta de estadísticas
                    AnimatedVisibility(
                        visible = total > 0,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        StatsCard(total = total, completed = completed, pending = pending)
                    }

                    // Filtros
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == "all",
                            onClick = { selectedFilter = "all" },
                            label = { Text("Todas") }
                        )
                        FilterChip(
                            selected = selectedFilter == "pending",
                            onClick = { selectedFilter = "pending" },
                            label = { Text("Pendientes") }
                        )
                        FilterChip(
                            selected = selectedFilter == "done",
                            onClick = { selectedFilter = "done" },
                            label = { Text("Completadas") }
                        )
                    }

                    val filtered = when (selectedFilter) {
                        "pending" -> state.tasks.filter { !it.isCompleted }
                        "done" -> state.tasks.filter { it.isCompleted }
                        else -> state.tasks
                    }

                    if (filtered.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (total == 0) "No hay tareas todavía" else "No hay tareas en esta categoría",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                                if (total == 0) {
                                    Text(
                                        text = "Tocá + para crear tu primera tarea",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtered, key = { it.id }) { task ->
                                TaskCard(
                                    task = task,
                                    onToggle = { viewModel.toggleCompleted(task) },
                                    onClick = { onTaskClick(task.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(total: Int, completed: Int, pending: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = total, label = "Total", color = MaterialTheme.colorScheme.onPrimaryContainer)
            VerticalDivider(modifier = Modifier.height(40.dp))
            StatItem(value = pending, label = "Pendientes", color = Color(0xFFFB8C00))
            VerticalDivider(modifier = Modifier.height(40.dp))
            StatItem(value = completed, label = "Completadas", color = Color(0xFF43A047))
        }
    }
}

@Composable
fun StatItem(value: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun TaskCard(task: Task, onToggle: () -> Unit, onClick: () -> Unit) {
    val priorityColor = when (task.priority) {
        "high" -> Color(0xFFE53935)
        "medium" -> Color(0xFFFB8C00)
        else -> Color(0xFF43A047)
    }
    val priorityLabel = when (task.priority) {
        "high" -> "Alta"
        "medium" -> "Media"
        else -> "Baja"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = "Completar",
                    tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotEmpty()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = priorityColor.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = priorityLabel,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor
                )
            }
        }
    }
}
