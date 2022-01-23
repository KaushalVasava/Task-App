package com.lahsuak.apps.mylist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_task_table")
data class SubTask(
    val id:Int,
    @ColumnInfo(name = "title") var title:String,
    @ColumnInfo(name = "status") var isDone:Boolean,
    @PrimaryKey(autoGenerate = true)
    val sId:Int
)
