package com.dailytaskmanager.app


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.dailytaskmanager.app.presentation.ui.task_list.TaskListScreen
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class TaskListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyTaskList_showsNoTasksMessage() {

        val viewModel = mockk<TaskViewModel>(relaxed = true)
        every { viewModel.taskList } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            TaskListScreen(
                viewModel = viewModel,
                onAddClick = {},
                onTaskClick = {}
            )
        }

        composeTestRule.onNodeWithText("No tasks available. Add one!")
            .assertIsDisplayed()
    }
}
