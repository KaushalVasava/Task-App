package com.lahsuak.apps.mylist.data.db

import androidx.room.*
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM task_table")
    fun getAllTodos(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Task)

    @Delete
    suspend fun delete(note: Task)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(note: Task)

    @Query("SELECT * FROM TASK_TABLE WHERE id=:todoID")
    suspend fun getById(todoID: Int): Task

    @Query("SELECT * FROM TASK_TABLE WHERE status=:isDone")
    fun getCompletedTask(isDone:Boolean): Flow<List<Task>>

    //subtask methods

    @Query("SELECT * FROM sub_task_table where id=:id")
    fun getAllSubTask(id: Int): Flow<List<SubTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(task: SubTask)

    @Delete
    suspend fun deleteSubTask(task: SubTask)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateSubTask(task: SubTask)

    @Query("SELECT * FROM SUB_TASK_TABLE WHERE id=:subtaskID")
    suspend fun getBySubTaskId(subtaskID: Int): SubTask

    @Query("SELECT * FROM SUB_TASK_TABLE WHERE status=:isDone")
    fun getCompletedSubTask(isDone:Boolean): Flow<List<SubTask>>
}
