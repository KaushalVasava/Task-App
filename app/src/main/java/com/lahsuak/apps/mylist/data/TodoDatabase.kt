package com.lahsuak.apps.mylist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.model.Task

@Database(
    entities = [Task::class,SubTask::class],
    version = 1
)
@TypeConverters(ListTypeConverter::class)
abstract class TodoDatabase: RoomDatabase() {

    abstract val dao: TodoDao

}