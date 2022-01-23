package com.lahsuak.apps.mylist.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.lahsuak.apps.mylist.data.relation.TaskWithSubTasks
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubTaskViewModel @ViewModelInject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    suspend fun getSubTask(id: Int): Flow<List<SubTask>> {
        return repository.getSubTasks(id)
      }
//    lateinit var f :MutableLiveData<>
    lateinit var subT: MutableLiveData<List<SubTask>> //repository.getSubTasks().asLiveData()

//     suspend fun getTaskBySubTasks(id: Int): List<SubTask> {
//         viewModelScope.launch {
//             for(i in 0..repository.getSubTasksOfTask(id).first().subTasks.size){
//                 subT.value = repository.getSubTasksOfTask(id).first().subTasks
//              }
//             withContext(Dispatchers.Main){
//                 return subT
//             }
//         }
//         return repository.getSubTasksOfTask(id)
//    }

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
}