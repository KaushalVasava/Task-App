package com.lahsuak.apps.mylist.data.repository

import com.lahsuak.apps.mylist.data.SortOrder
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun insertTodo(todo: Task)

    suspend fun deleteTodo(todo: Task)

    suspend fun updateTodo(todo: Task)

    fun getAllTasks(
        searchQuery: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Task>>

    suspend fun getById(id: Int): Task

    suspend fun deleteAllCompletedTask()

    suspend fun deleteAllTasks()
    //subtask methods
    suspend fun insertSubTask(todo: SubTask)

    suspend fun deleteSubTask(todo: SubTask)

    suspend fun updateSubTask(todo: SubTask)

    fun getAllSubTasks(
        id: Int,
        query: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<SubTask>>

    suspend fun deleteAllCompletedSubTask()

    suspend fun deleteAllSubTasks(id: Int)
}
