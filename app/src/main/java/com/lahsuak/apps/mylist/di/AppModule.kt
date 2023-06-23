package com.lahsuak.apps.mylist.di

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.room.Room
import com.lahsuak.apps.mylist.data.TodoDatabase
import com.lahsuak.apps.mylist.repository.TodoRepository
import com.lahsuak.apps.mylist.repository.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): TodoDatabase {
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db:TodoDatabase):TodoRepository{
        return TodoRepositoryImpl(db.dao)
    }
    fun notifyUser(context:Context,msg: String){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
    }
}