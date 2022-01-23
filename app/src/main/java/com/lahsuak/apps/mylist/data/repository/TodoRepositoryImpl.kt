package com.lahsuak.apps.mylist.data.repository

import com.lahsuak.apps.mylist.data.db.TodoDao
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl(
    private val dao: TodoDao
) : TodoRepository {

    override suspend fun insertTodo(todo: Task) {
        dao.insert(todo)
    }

    override suspend fun deleteTodo(todo: Task) {
        dao.delete(todo)
    }

    override suspend fun updateTodo(todo: Task) {
        dao.update(todo)
    }

    override fun getTodos(): Flow<List<Task>> {
        return dao.getAllTodos()
    }

    override suspend fun getById(id: Int): Task {
        return dao.getById(id)
    }

    override fun getCompletedTask(isDone:Boolean): Flow<List<Task>> {
        return dao.getCompletedTask(isDone)
    }
    //subtask methods

    override suspend fun insertSubTask(todo: SubTask) {
        dao.insertSubTask(todo)
    }

    override suspend fun deleteSubTask(todo: SubTask) {
        dao.deleteSubTask(todo)
    }

    override suspend fun updateSubTask(todo: SubTask) {
        dao.updateSubTask(todo)
    }

    override fun getSubTasks(id:Int): Flow<List<SubTask>> {
        return dao.getAllSubTask(id)
    }

    override suspend fun getBySubTaskId(id:Int):SubTask {
        return dao.getBySubTaskId(id)
    }

    override fun getCompletedSubTask(isDone: Boolean): Flow<List<SubTask>> {
        return dao.getCompletedSubTask(isDone)
    }
}