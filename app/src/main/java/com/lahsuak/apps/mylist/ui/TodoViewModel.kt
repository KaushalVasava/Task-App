package com.lahsuak.apps.mylist.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lahsuak.apps.mylist.databinding.RenameDialogBinding
import com.lahsuak.apps.mylist.model.Task
import com.lahsuak.apps.mylist.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel  @ViewModelInject constructor(
    private val repository: TodoRepository
): ViewModel() {
    val todos = repository.getTodos().asLiveData()

    fun insert(todo: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertTodo(todo)
    }

    fun update(todo: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTodo(todo)
    }
    fun delete(todo: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTodo(todo)
    }
    suspend fun getById(id: Int):Task {
        return repository.getById(id)
    }

    fun showDeleteDialog(context: Context, isDone:Boolean,id: Int,list:ArrayList<Task>){
        if (isDone) {
            AlertDialog.Builder(context)
                .setTitle("Delete?")
                .setMessage("Do you want to delete?")
                .setPositiveButton("Delete") { dialog, _ ->
                    viewModelScope.launch {
                        val todo = getById(id)
                        delete(todo)
                        list.remove(todo)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            addOrRenameDialog(context,false,id,LayoutInflater.from(context))
        }
    }
    fun addOrRenameDialog(context:Context,isNew: Boolean,id: Int,layoutInflater:LayoutInflater) {
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
        //   renameBinding.input.requestFocus()
        builder.setView(renameBinding.root)
            .setTitle(title)
            .setPositiveButton(ok) { dialog, _ ->
                val rename = renameBinding.renameText.text.toString()
                if (rename.isNotEmpty()) {
                    //rename title
                    if (!isNew) {
                        viewModelScope.launch {
                            val todo = getById(id)
                            todo.title = rename
                            update(todo)
                            // adapter.notifyDataSetChanged()
                        }
                    } else {
                        val todo = Task(0,null,rename,false,)
                        insert(todo)
                    }
                    dialog.dismiss()
                } else {
                    //
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}