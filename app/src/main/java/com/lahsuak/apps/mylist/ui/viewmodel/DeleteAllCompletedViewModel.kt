package com.lahsuak.apps.mylist.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.lahsuak.apps.mylist.data.repository.TodoRepository
import com.lahsuak.apps.mylist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val repository: TodoRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        repository.deleteAllCompletedTask()
    }

    fun onConfirmClick2() = applicationScope.launch {
        repository.deleteAllCompletedSubTask()
    }

    fun deleteAllTasks() = applicationScope.launch {
        repository.deleteAllTasks()
    }

//    fun deleteAllSubTasks() = applicationScope.launch {
//        repository.deleteAllSubTasks()
//    }
}