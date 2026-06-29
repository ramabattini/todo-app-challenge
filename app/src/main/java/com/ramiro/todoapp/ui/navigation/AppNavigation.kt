package com.ramiro.todoapp.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ramiro.todoapp.ui.screens.TaskDetailScreen
import com.ramiro.todoapp.ui.screens.TaskFormScreen
import com.ramiro.todoapp.ui.screens.TaskListScreen
import com.ramiro.todoapp.ui.viewmodel.TaskViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: TaskViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "task_list",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) {
        composable("task_list") {
            TaskListScreen(
                viewModel = viewModel,
                onTaskClick = { taskId -> navController.navigate("task_detail/$taskId") },
                onAddClick = { navController.navigate("task_form") }
            )
        }
        composable(
            route = "task_detail/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                viewModel = viewModel,
                onEditClick = { navController.navigate("task_form?taskId=$taskId") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "task_form?taskId={taskId}",
            arguments = listOf(navArgument("taskId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            TaskFormScreen(
                taskId = if (taskId == -1L) null else taskId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
