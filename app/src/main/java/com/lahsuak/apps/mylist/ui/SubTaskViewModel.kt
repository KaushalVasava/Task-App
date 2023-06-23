package com.lahsuak.apps.mylist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubTaskViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    fun getSubTask(id: Int): Flow<List<SubTask>> {
        return repository.getSubTasks(id)
    }

    fun insertSubTask(subTask: SubTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertSubTask(subTask)
    }

    fun updateSubTask(subTask: SubTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateSubTask(subTask)
    }

    fun deleteSubTask(subTask: SubTask) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSubTask(subTask)
    }
}