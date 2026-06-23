package com.dailytaskmanager.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.dailytaskmanager.app.presentation.ui.task_form.TaskFormScreen
import com.dailytaskmanager.app.presentation.ui.task_list.TaskListScreen
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel

sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object TaskForm : Screen("task_form?taskId={taskId}") {
        fun createRoute(taskId: Int? = null): String {
            return if (taskId == null) "task_form" else "task_form?taskId=$taskId"
        }
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val viewModel: TaskViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Screen.TaskList.route) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate(Screen.TaskForm.createRoute()) },
                onTaskClick = { taskId -> navController.navigate(Screen.TaskForm.createRoute(taskId)) }
            )
        }
        composable(
            route = Screen.TaskForm.route,
            arguments = listOf(navArgument("taskId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")?.takeIf { it != -1 }
            TaskFormScreen(
                viewModel = viewModel,
                taskId = taskId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
