package com.lahsuak.apps.mylist.ui.fragments

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.data.relation.TaskWithSubTasks
import com.lahsuak.apps.mylist.databinding.FragmentAddUpdateBinding
import com.lahsuak.apps.mylist.databinding.RenameDialogBinding
import com.lahsuak.apps.mylist.di.AppModule
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.model.Task
import com.lahsuak.apps.mylist.ui.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddUpdateFragment : Fragment(R.layout.fragment_add_update), SubTaskListener {
    private lateinit var binding: FragmentAddUpdateBinding
    private val model: TodoViewModel by viewModels()
    private val subModel: SubTaskViewModel by viewModels()
    private val args: AddUpdateFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var adapter: SubTaskAdapter
    private lateinit var todo: Task
    //   private var sub: mutab

   // companion object {
        var subList = mutableListOf<SubTask>()
    //}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddUpdateBinding.bind(view)
        adapter = SubTaskAdapter(requireContext(), subList, this)
        binding.todoRecyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            todo = model.getById(args.id)
//            val to = subModel.getTaskBySubTasks(args.id)
//            val lit = to.first().subTasks
        }
        navController = findNavController()

        viewLifecycleOwner.lifecycleScope.launch {
            subModel.getSubTask(args.id).asLiveData().observe(viewLifecycleOwner) {
                subList.clear()

//                val to = subModel.getTaskBySubTasks(args.id)
//                val lit = to.first().task.id
//                Log.d("TAG", "onViewCreated: # $lit")
//                for (index in it) {
//                    if (index.id == lit) {
//                        subList.add(index)
//                    }
//                }

//            for(index in it){
//                if(index.todoId==todo.id){
//                    subList.add(index)
//                }
//            }
                subList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }

        binding.addBtn.setOnClickListener {
            addOrRenameDialog(true, 0)
        }
    }

    override fun onItemClicked(position: Int, todo: SubTask) {
//noting to do
    }

    override fun onDeleteClicked(id: Int, isDone: Boolean) {
        showDeleteDialog(isDone, id)
    }

    override fun onCheckBoxClicked(
        position: Int,
        todo: SubTask,
        isChecked: Boolean,
        holder: SubTaskAdapter.SubTaskViewHolder
    ) {
        if (!isChecked) {
            holder.checkBox.isChecked = false
            todo.isDone = false
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_edit)
        } else {
            holder.checkBox.isChecked = true
            todo.isDone = true
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_delete)
        }
    }

    private fun addOrRenameDialog(isNew: Boolean, id: Int) {
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
        val builder = MaterialAlertDialogBuilder(requireContext())
        //   renameBinding.input.requestFocus()
        builder.setView(renameBinding.root)
            .setTitle(title)
            .setPositiveButton(ok) { dialog, _ ->
                val rename = renameBinding.renameText.text.toString()
                if (rename.isNotEmpty()) {
                    //rename title
                    if (!isNew) {
                        lifecycleScope.launch {
                            val task = subModel.getSubTaskById(id)
                            task.title = rename
                            subModel.updateSubTask(task)
                        }
                    } else {
                        val todo1 = SubTask(todo.id,rename, false, 0)
                        subModel.insertSubTask(todo1)
                        subList.add(todo1)
                        val f = Task( args.id,subList,todo.title, todo.isDone,)
                        model.update(f)
                    }
                    dialog.dismiss()
                } else {
                    AppModule.notifyUser(requireContext(),"Please enter title")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteDialog(isDone: Boolean, id: Int) {
        if (isDone) {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete?")
                .setMessage("Do you want to delete?")
                .setPositiveButton("Delete") { dialog, _ ->
                    lifecycleScope.launch {
                        val todo = subModel.getSubTaskById(id)
                        subModel.deleteSubTask(todo)
                        subList.remove(todo)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            addOrRenameDialog(false, id)
        }
    }

}