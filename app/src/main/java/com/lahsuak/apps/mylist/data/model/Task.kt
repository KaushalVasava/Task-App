package com.lahsuak.apps.mylist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "status") val isDone: Boolean = false,
    @ColumnInfo(name = "importance") var isImp: Boolean = false,
    @ColumnInfo(name = "reminder") var reminder: String? = null
)