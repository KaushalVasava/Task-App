package com.lahsuak.apps.mylist.data.repository

import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    suspend fun insertTodo(todo: Task)

    suspend fun deleteTodo(todo: Task)

    suspend fun updateTodo(todo: Task)

    fun getTodos(): Flow<List<Task>>

    suspend fun getById(id:Int):Task

    fun getCompletedTask(isDone:Boolean):Flow<List<Task>>

    //subtask methods
    suspend fun insertSubTask(todo: SubTask)

    suspend fun deleteSubTask(todo: SubTask)

    suspend fun updateSubTask(todo: SubTask)

    fun getSubTasks(id:Int): Flow<List<SubTask>>

    suspend fun getBySubTaskId(id:Int):SubTask

    fun getCompletedSubTask(isDone:Boolean):Flow<List<SubTask>>

}
