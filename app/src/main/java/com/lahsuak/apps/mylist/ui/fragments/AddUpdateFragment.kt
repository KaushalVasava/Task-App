package com.lahsuak.apps.mylist.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.databinding.FragmentAddUpdateBinding
import com.lahsuak.apps.mylist.databinding.RenameDialogBinding
import com.lahsuak.apps.mylist.di.AppModule
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.ui.adapters.SubTaskAdapter
import com.lahsuak.apps.mylist.ui.adapters.SubTaskListener
import com.lahsuak.apps.mylist.ui.viewmodel.SubTaskViewModel
import com.lahsuak.apps.mylist.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddUpdateFragment : Fragment(R.layout.fragment_add_update), SubTaskListener,SearchView.OnQueryTextListener {
    private lateinit var binding: FragmentAddUpdateBinding
    private val model: TodoViewModel by viewModels()
    private val subModel: SubTaskViewModel by viewModels()
    private val args: AddUpdateFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var adapter: SubTaskAdapter
    private lateinit var todo: Task
    private var subList = mutableListOf<SubTask>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddUpdateBinding.bind(view)
        adapter = SubTaskAdapter(requireContext(), subList, this)
        binding.todoRecyclerView.adapter = adapter

        setHasOptionsMenu(true)
         viewLifecycleOwner.lifecycleScope.launch {
            todo = model.getById(args.id)
        }
        navController = findNavController()

        viewLifecycleOwner.lifecycleScope.launch {
            subModel.getSubTask(args.id).asLiveData().observe(viewLifecycleOwner) {
                subList.clear()
                subList.addAll(it)
                adapter.notifyDataSetChanged()
//                adapter.updateList(it as java.util.ArrayList<SubTask>)
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
  //      adapter.updateList(subList as java.util.ArrayList<SubTask>)
    }

    override fun onCheckBoxClicked(
        position: Int,
        subTask: SubTask,
        isChecked: Boolean,
        holder: SubTaskAdapter.SubTaskViewHolder
    ) {
        if (!isChecked) {
            holder.checkBox.isChecked = false
            subTask.isDone = false
            subModel.updateSubTask(subTask)
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_edit)
           // subList[position].isDone= false
        } else {
            holder.checkBox.isChecked = true
            subTask.isDone = true
            subModel.updateSubTask(subTask)
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_delete)
          //  subList[position].isDone= true
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
        builder.setView(renameBinding.root)
            .setTitle(title)
            .setPositiveButton(ok) { dialog, _ ->
                val rename = renameBinding.renameText.text.toString()
                if (rename.isNotEmpty()) {
                    if (!isNew) {
                        lifecycleScope.launch {
                            val subTask = subModel.getSubTaskById(id)
                            subTask.title = rename
                            subModel.updateSubTask(subTask)
                        }
                    } else {
                        val todo1 = SubTask(todo.id,rename, false, 0)
                        subModel.insertSubTask(todo1)
                        subList.add(todo1)
                        val task = Task( args.id,subList,todo.title, todo.isDone,)
                        model.update(task)
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = "Search Task"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {}
            R.id.showTask -> {

                if(item.isChecked)
                    item.isChecked = false
                else
                    item.isChecked = true
                subModel.getCompletedSubTask(item.isChecked).observe(viewLifecycleOwner) {
                    subList.clear()
                    subList.addAll(it as MutableList<SubTask>)
                    adapter.notifyDataSetChanged()
                }
            }
            R.id.setting -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val input = newText!!.lowercase(Locale.getDefault())
        val myFiles = ArrayList<SubTask>()
        for (item in subList) {
            if (item.title.lowercase(Locale.getDefault()).contains(input)) {
                myFiles.add(item)
            }
        }
        adapter.updateList(myFiles)

        return true
    }
}