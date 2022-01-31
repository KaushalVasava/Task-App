package com.lahsuak.apps.mylist.data.repository

import com.lahsuak.apps.mylist.data.SortOrder
import com.lahsuak.apps.mylist.data.db.TodoDao
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.data.relation.TaskWithSubTasks
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

    override fun getAllTasks(
        searchQuery: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Task>> {
        return dao.getAllTasks(searchQuery, sortOrder, hideCompleted)
    }

    override suspend fun getById(id: Int): Task {
        return dao.getById(id)
    }

    override suspend fun deleteAllCompletedTask() {
        dao.deleteAllCompletedTask()
    }

    override suspend fun deleteAllTasks() {
       dao.deleteAllTask()
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

    override fun getAllSubTasks(
        id: Int,
        query: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<SubTask>> {
        return dao.getAllSubTasks(id, query, sortOrder, hideCompleted)
    }

    override suspend fun deleteAllCompletedSubTask() {
        dao.deleteAllCompletedSubTask()
    }

    override suspend fun deleteAllSubTasks(id: Int) {
        dao.deleteAllSubTask(id)
    }

    override suspend fun getBySubTaskId(id: Int): SubTask {
        return dao.getBySubTaskId(id)
    }
}