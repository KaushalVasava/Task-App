package com.lahsuak.apps.mylist.model

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    @ColumnInfo(name = "subtask") var subtask:List<SubTask>?,
    @ColumnInfo(name = "title") var title:String,
    @ColumnInfo(name = "status") var isDone:Boolean
)
//  @ColumnInfo(name = "subtask")var subTask: List<SubTask>? =null,
