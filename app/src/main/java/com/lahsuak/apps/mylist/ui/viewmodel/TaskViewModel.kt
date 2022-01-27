package com.lahsuak.apps.mylist.ui.viewmodel

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lahsuak.apps.mylist.data.PreferenceManager
import com.lahsuak.apps.mylist.data.SortOrder
import com.lahsuak.apps.mylist.databinding.RenameDialogBinding
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.data.repository.TodoRepository
import com.lahsuak.apps.mylist.ui.adapters.TaskAdapter
import com.lahsuak.apps.mylist.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val repository: TodoRepository,
    private val preferenceManager: PreferenceManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    sealed class TaskEvent {
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
        object NavigateToAllCompletedScreen :TaskEvent()
    }

    var isNewTaskClicked = MutableLiveData<Boolean>(false)
    val searchQuery = state.getLiveData("searchQuery","")//MutableStateFlow("")

    val preferencesFlow = preferenceManager.preferencesFlow
    private val taskEventChannel = Channel<TaskEvent>()
    val tasksEvent = taskEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
        searchQuery.asFlow(), preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        repository.getAllTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val todos = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun onHideCompleted(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        repository.deleteTodo(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        repository.updateTodo(task.copy(isDone = isChecked))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        repository.insertTodo(task)
    }

    fun onDeleteAllCompletedClick() =viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAllCompletedScreen)
    }

    fun insert(todo: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTodo(todo)
    }

    fun update(todo: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTodo(todo)
    }

    fun delete(todo: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTodo(todo)
    }

    suspend fun getById(id: Int): Task {
        return repository.getById(id)
    }
    suspend fun deleteAllTasks() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllTasks()
    }

    fun showDeleteDialog(
        context: Context,
        task: Task,
        position: Int,
        adapter: TaskAdapter
    ) {
        if (task.isDone) {
            AlertDialog.Builder(context)
                .setTitle("Delete?")
                .setMessage("Do you want to delete?")
                .setPositiveButton("Delete") { dialog, _ ->
                    viewModelScope.launch {
                        delete(task)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            addOrRenameDialog(context, false, task, position, LayoutInflater.from(context), adapter)
        }
    }

    fun addOrRenameDialog(
        context: Context,
        isNew: Boolean,
        task: Task?,
        position: Int,
        layoutInflater: LayoutInflater, adapter: TaskAdapter
    ) {
        val title: String
        val ok: String
        if (isNew) {
            title = "New Task"
            ok = "Add"
        } else {
            title = "Edit Task"
            ok = "Rename"
        }

        val renameBinding = RenameDialogBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(context)
        var imp = false
        var isChanged = false
        if (task != null) {
            renameBinding.impTask.isChecked = task.isImp
            renameBinding.renameText.setText(task.title)
        }
        renameBinding.impTask.setOnCheckedChangeListener { _, isChecked ->
            imp = isChecked
            isChanged = true
        }
        builder.setView(renameBinding.root)
            .setTitle(title)
            .setPositiveButton(ok) { dialog, _ ->
                val rename = renameBinding.renameText.text.toString()
                if (rename.isNotEmpty()) {
                    if (!isNew) {
                        viewModelScope.launch {
                            task!!.title = rename.trim()
                            if(isChanged)
                                task.isImp = imp
                            update(task)
                            adapter.notifyItemChanged(position)
                        }
                    } else {
                        val todo = Task(0, rename.trim(), false, imp)
                        insert(todo)
                    }
                    dialog.dismiss()
                } else {
                    Util.notifyUser(context, "Please enter title")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}