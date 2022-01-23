package com.lahsuak.apps.mylist.repository

import com.lahsuak.apps.mylist.data.TodoDao
import com.lahsuak.apps.mylist.data.relation.TaskWithSubTasks
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

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
//    override suspend fun getSubTask(id: Int): List<Todo>? {
//        return dao.getSubTask(id)
//    }

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

//    override suspend fun getSubTasksOfTask(id: Int): List<SubTask> {
//       return dao.getTaskBySubTasks(id)
//    }

}