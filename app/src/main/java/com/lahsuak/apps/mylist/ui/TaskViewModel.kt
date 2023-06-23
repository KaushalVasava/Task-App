package com.lahsuak.apps.mylist.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.databinding.RenameDialogBinding
import com.lahsuak.apps.mylist.model.Task
import com.lahsuak.apps.mylist.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    val tasks = repository.getTodos().asLiveData()

    private fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTodo(task)
    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTodo(task)
    }

    private fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTodo(task)
    }

    suspend fun getById(id: Int): Task {
        return repository.getById(id)
    }

    fun showDeleteDialog(context: Context, isDone: Boolean, task: Task) {
        if (isDone) {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.delete_confirm))
                .setPositiveButton(context.getString(R.string.delete)) { dialog, _ ->
                    delete(task)
                    dialog.dismiss()
                }
                .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            addOrRenameDialog(context, task)
        }
    }

    fun addOrRenameDialog(
        context: Context,
        task: Task?
    ) {
        val title: String
        val ok = if (task == null) {
            title = context.getString(R.string.new_task)
            context.getString(R.string.add)
        } else {
            title = context.getString(R.string.edit_task)
            context.getString(R.string.rename)
        }

        val renameBinding = RenameDialogBinding.inflate(LayoutInflater.from(context))
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(renameBinding.root)
            .setTitle(title)
            .setPositiveButton(ok) { dialog, _ ->
                val rename = renameBinding.renameText.text.toString()
                if (rename.isNotEmpty()) {
                    //rename title
                    if (task != null) {
                        update(task.copy(title = rename))
                    } else {
                        val newTask = Task(0, null, rename, false)
                        insert(newTask)
                    }
                    dialog.dismiss()
                } else {
                    // do nothing
                }
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}