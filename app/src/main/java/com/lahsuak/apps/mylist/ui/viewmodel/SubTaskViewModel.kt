package com.lahsuak.apps.mylist.ui.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.lahsuak.apps.mylist.data.PreferenceManager
import com.lahsuak.apps.mylist.data.SortOrder
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SubTaskViewModel @ViewModelInject constructor(
    private val repository: TodoRepository,
    private val preferenceManager: PreferenceManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    sealed class SubTaskEvent {
        data class ShowUndoDeleteTaskMessage(val subTask: SubTask) : SubTaskEvent()
        object NavigateToAllCompletedScreen :SubTaskEvent()
    }

    val searchQuery = state.getLiveData("searchQuery","")
    val taskId = state.getLiveData("taskId",0)//MutableStateFlow(0)

    val preferencesFlow = preferenceManager.preferencesFlow2
    private val subTaskEventChannel = Channel<SubTaskEvent>()
    val subTasksEvent = subTaskEventChannel.receiveAsFlow()

    private val subTasksFlow = combine(
        taskId.asFlow(), searchQuery.asFlow(), preferencesFlow
    ) { tId, query, filterPreferences ->
        Triple(tId, query, filterPreferences)
    }.flatMapLatest { (tid, query, filterPreferences) ->
        repository.getAllSubTasks(
            tid,
            query,
            filterPreferences.sortOrder,
            filterPreferences.hideCompleted
        )
    }

    val subTasks = subTasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder2(sortOrder)
    }

    fun onHideCompleted(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted2(hideCompleted)
    }

    fun onSubTaskSwiped(subTask: SubTask) = viewModelScope.launch {
        repository.deleteSubTask(subTask)
        subTaskEventChannel.send(SubTaskEvent.ShowUndoDeleteTaskMessage(subTask))
    }

    fun onSubTaskCheckedChanged(subTask: SubTask, isChecked: Boolean) = viewModelScope.launch {
        repository.updateSubTask(subTask.copy(isDone = isChecked))
    }

    fun onUndoDeleteClick(subTask: SubTask) = viewModelScope.launch {
        repository.insertSubTask(subTask)
    }

    fun onDeleteAllCompletedClick() =viewModelScope.launch {
        subTaskEventChannel.send(SubTaskEvent.NavigateToAllCompletedScreen)
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

    fun deleteAllSubTasks(id: Int) =viewModelScope.launch(Dispatchers.IO){
        repository.deleteAllSubTasks(id)
    }
}