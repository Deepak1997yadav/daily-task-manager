package com.dailytaskmanager.app.di

import android.app.Application
import androidx.room.Room
import com.dailytaskmanager.app.data.local.TaskDatabase
import com.dailytaskmanager.app.data.repository.TaskRepositoryImpl
import com.dailytaskmanager.app.domain.repository.TaskRepository
import com.dailytaskmanager.app.domain.usecase.*
import com.dailytaskmanager.app.domain.usecase.TaskUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(app: Application): TaskDatabase {
        return Room.databaseBuilder(
            app,
            TaskDatabase::class.java,
            "task_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()

    @Provides
    @Singleton
    fun provideTaskRepository(db: TaskDatabase): TaskRepository {
        return TaskRepositoryImpl(db.taskDao())
    }

    @Provides
    @Singleton
    fun provideTaskUseCases(repository: TaskRepository): TaskUseCases {
        return TaskUseCases(
            addTask = AddTaskUseCase(repository),
            updateTask = UpdateTaskUseCase(repository),
            deleteTask = DeleteTaskUseCase(repository),
            getAllTasks = GetAllTasksUseCase(repository),
            getTaskById = GetTaskByIdUseCase(repository)
        )
    }
}
