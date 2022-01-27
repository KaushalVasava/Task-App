package com.lahsuak.apps.mylist.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.data.SortOrder
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.databinding.FragmentListBinding
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.ui.MainActivity.Companion.shareTxt
import com.lahsuak.apps.mylist.ui.adapters.TaskAdapter
import com.lahsuak.apps.mylist.ui.viewmodel.TaskViewModel
import com.lahsuak.apps.mylist.util.Util.notifyUser
import com.lahsuak.apps.mylist.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), TaskAdapter.TaskListener {
    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController
    private val model: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var searchView: SearchView
    private var actionMode: ActionMode? = null

    companion object {
        var selectedItem: Array<Boolean>? = null
        var counter = 0
        var is_in_action_mode = false
        var is_select_all = false
    }

    //private var todoList = mutableListOf<Task>()

    @SuppressLint("NotifyDataSetChanged", "QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)
        taskAdapter = TaskAdapter(requireContext(), this)

        setHasOptionsMenu(true)

        navController = findNavController()

        if(shareTxt!=null){
             val task = Task(0, shareTxt!!,false,false)
            model.addOrRenameDialog(requireContext(), true, task, -1, layoutInflater, taskAdapter)
        }

        binding.apply {
            todoRecyclerView.apply {
                adapter = taskAdapter
                setHasFixedSize(true)
            }
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    model.onTaskSwiped(task)
                }
            }).attachToRecyclerView(todoRecyclerView)
        }
        val speakLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val result1 = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val task = Task(0, result1!![0], isDone = false, false)
                    model.insert(task)
                }
            }

        model.todos.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
            var count = 0
            for (element in it) {
                if (element.isDone)
                    count++
            }
            binding.taskProgress.text = getString(R.string.task_progress, count, it.size)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.tasksEvent.collect { event ->
                when (event) {
                    is TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                model.onUndoDeleteClick(event.task)
                            }.show()
                    }
                    TaskViewModel.TaskEvent.NavigateToAllCompletedScreen -> {
                        val action =
                            ListFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        navController.navigate(action)
                    }
                    else -> {}
                }
            }
        }
        binding.fab.setOnClickListener {
            model.addOrRenameDialog(requireContext(), true, null, -1, layoutInflater, taskAdapter)
        }
        binding.soundTask.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                //startActivityForResult(intent, 1)
                speakLauncher.launch(intent)
            } else {
                notifyUser(
                    requireContext(),
                    "Your device not supported for speech input"
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = model.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }
        searchView.onQueryTextChanged {
            model.searchQuery.value = it
        }
        searchView.queryHint = "Search Task"
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.showTask).isChecked = model.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sortByName -> {
                model.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.sortByOld -> {
                model.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.showTask -> {
                item.isChecked = !item.isChecked
                model.onHideCompleted(item.isChecked)
                true
            }
            R.id.delete_all_completed_task -> {
                model.onDeleteAllCompletedClick()
                true
            }
            R.id.setting -> {
                val action = ListFragmentDirections.actionListFragmentToSettingsFragment()
                navController.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClicked(task: Task, position: Int) {
        if (is_in_action_mode) {
            if (selectedItem!![position]) {
                selectedItem!![position] = false
                counter--
                actionMode!!.title = "${counter}/${taskAdapter.currentList.size} Selected"
            } else {
                selectedItem!![position] = true
                counter++
                actionMode!!.title = "${counter}/${taskAdapter.currentList.size} Selected"
            }
        } else if (!is_in_action_mode) {
            val action =
                ListFragmentDirections.actionListFragmentToAddUpdateFragment(
                    task.id,
                    task.title
                )
            navController.navigate(action)
        }
    }

    override fun onCheckBoxClicked(
        task: Task,
        taskCompleted: Boolean
    ) {
        model.onTaskCheckedChanged(task, taskCompleted)
    }

    override fun onDeleteClicked(task: Task, position: Int) {
        model.showDeleteDialog(requireContext(), task, position, taskAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        shareTxt = null
    }

    private val callback = object : ActionMode.Callback {
        override fun onCreateActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            val menuInflater = MenuInflater(requireContext())
            menuInflater.inflate(R.menu.action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onActionItemClicked(
            mode: ActionMode?,
            item: MenuItem?
        ): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    if (counter == 0) {
                        Toast.makeText(
                            requireContext(),
                            "Please select a task",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (selectedItem!!.isNotEmpty()) {
                        if (counter == taskAdapter.currentList.size) {
                            //delete all tasks
                            showDialog(true)
                        } else {
                            //delete one by one
                            showDialog(false)
                        }
                    }
                    true
                }
                R.id.action_selectAll -> {
                    if (!is_select_all) {
                        item.setIcon(R.drawable.ic_select_all_on)
                        for (i in 0 until taskAdapter.currentList.size)
                            selectedItem!![i] == true

                        counter = taskAdapter.currentList.size
                        actionMode!!.title =
                            "${counter}/${taskAdapter.currentList.size} Selected"
                        is_select_all = true
                    } else {
                        item.setIcon(R.drawable.ic_select_all)
                        for (i in 0 until taskAdapter.currentList.size)
                            selectedItem!![i] == false

                        counter = 0
                        is_select_all = false
                        actionMode!!.title =
                            "${counter}/${taskAdapter.currentList.size} Selected"
                    }
                    taskAdapter.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            onActionMode(false)
            actionMode = null
        }
    }

    override fun onAnyItemLongClicked(position: Int) {
        if (!is_in_action_mode) {
            onActionMode(true)
            counter = 1
            selectedItem!![position] = true
        } else {
            if (selectedItem!![position]) {
                selectedItem!![position] = false
                counter--
            } else {
                selectedItem!![position] = true
                counter++
            }
        }
        if (actionMode == null) {
            actionMode =
                (activity as AppCompatActivity).startSupportActionMode(callback)!!
        }
        actionMode!!.title = "${counter}/${taskAdapter.currentList.size} Selected"
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onActionMode(actionModeOn: Boolean) {
        if (actionModeOn) {
            selectedItem = Array(taskAdapter.currentList.size) { false }
            is_in_action_mode = true
            binding.soundTask.visibility = View.GONE
            binding.fab.visibility = View.GONE
            //  taskAdapter.notifyDataSetChanged()
        } else {
            is_in_action_mode = false
            is_select_all = false
            binding.fab.visibility = View.VISIBLE
            binding.soundTask.visibility = View.VISIBLE
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun showDialog(isAllDeleted: Boolean) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you want to delete all the tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { dialog, _ ->
                if (isAllDeleted) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        model.deleteAllTasks()
                    }
                } else {
                    for (i in selectedItem!!.indices) {
                        if (selectedItem!![i]) {
                            model.delete(taskAdapter.currentList[i])
                        }
                    }
                }
                counter = 0
                actionMode!!.finish()
                onActionMode(false)
                notifyUser(requireContext(), "Tasks are deleted successfully")
                dialog.dismiss()
            }.show()
    }

}