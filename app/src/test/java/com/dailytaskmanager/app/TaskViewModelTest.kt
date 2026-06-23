package com.dailytaskmanager.app

import app.cash.turbine.test
import com.dailytaskmanager.app.domain.model.Task
import com.dailytaskmanager.app.domain.usecase.*
import com.dailytaskmanager.app.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    private lateinit var viewModel: TaskViewModel
    private lateinit var taskUseCases: TaskUseCases
    private val testDispatcher = StandardTestDispatcher()

    private val fakeTasks = listOf(
        Task(id = 1, title = "Test 1", description = "Desc 1", timestamp = 1L),
        Task(id = 2, title = "Test 2", description = "Desc 2", timestamp = 2L)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val fakeRepo = FakeTaskRepository()
        fakeRepo.setTasks(fakeTasks)

        taskUseCases = TaskUseCases(
            addTask = AddTaskUseCase(fakeRepo),
            updateTask = UpdateTaskUseCase(fakeRepo),
            deleteTask = DeleteTaskUseCase(fakeRepo),
            getAllTasks = GetAllTasksUseCase(fakeRepo),
            getTaskById = GetTaskByIdUseCase(fakeRepo)
        )
        viewModel = TaskViewModel(taskUseCases)
    }

    @Test
    fun `fetch all tasks updates taskList`() = runTest {
        advanceUntilIdle()
        assertEquals(2, viewModel.taskList.value.size)
    }

    @Test
    fun `add task updates repository`() = runTest {
        val task = Task(title = "New", description = "Desc", timestamp = 123L)
        viewModel.addTask(task)
        advanceUntilIdle()
        assertEquals(3, viewModel.taskList.value.size)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
