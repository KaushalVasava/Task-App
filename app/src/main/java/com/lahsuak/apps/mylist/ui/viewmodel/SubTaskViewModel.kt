package com.lahsuak.apps.mylist.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.data.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SubTaskViewModel @ViewModelInject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    fun getSubTask(id: Int): Flow<List<SubTask>> {
        return repository.getSubTasks(id)
    }

    fun insertSubTask(todo: SubTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertSubTask(todo)
    }

    fun updateSubTask(todo: SubTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateSubTask(todo)
    }

    fun deleteSubTask(todo: SubTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSubTask(todo)
    }

    suspend fun getSubTaskById(id: Int): SubTask {
        return repository.getBySubTaskId(id)
    }

    fun getCompletedSubTask(isDone: Boolean): LiveData<List<SubTask>> {
        return repository.getCompletedSubTask(isDone).asLiveData()
    }
}