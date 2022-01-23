package com.lahsuak.apps.mylist.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task

@Database(
    entities = [Task::class,SubTask::class],
    version = 1
)
@TypeConverters(ListTypeConverter::class)
abstract class TodoDatabase: RoomDatabase() {

    abstract val dao: TodoDao

}