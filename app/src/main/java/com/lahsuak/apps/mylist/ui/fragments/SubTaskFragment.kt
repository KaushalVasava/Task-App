package com.lahsuak.apps.mylist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.databinding.FragmentSubtaskBinding
import com.lahsuak.apps.mylist.databinding.RenameDialogBinding
import com.lahsuak.apps.mylist.di.AppModule
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.model.Task
import com.lahsuak.apps.mylist.ui.*
import com.lahsuak.apps.mylist.ui.adapter.SubTaskAdapter
import com.lahsuak.apps.mylist.util.ItemClickedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubTaskFragment : Fragment(R.layout.fragment_subtask), ItemClickedListener<SubTask> {

    private var _binding: FragmentSubtaskBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by viewModels()
    private val subtaskViewModel: SubTaskViewModel by viewModels()
    private val args: SubTaskFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var subtaskAdapter: SubTaskAdapter
    private lateinit var task: Task

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSubtaskBinding.bind(view)
        navController = findNavController()

        // recyclerview and adapter initialization
        subtaskAdapter = SubTaskAdapter(this)
        binding.taskRecyclerView.apply {
            setHasFixedSize(true)
            adapter = subtaskAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            task = taskViewModel.getById(args.id)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            subtaskViewModel.getSubTask(args.id).asLiveData().observe(viewLifecycleOwner) {
                subtaskAdapter.submitList(it)
            }
        }

        binding.btnAdd.setOnClickListener {
            addOrRenameDialog(null)
        }
    }

    override fun onItemClicked(item: SubTask, position: Int) {
        addOrRenameDialog(item)
    }

    override fun onDeleteClicked(item: SubTask, isDone: Boolean, position: Int) {
        showDeleteDialog(isDone, item)
    }

    override fun onCheckBoxClicked(item: SubTask, isTaskCompleted: Boolean) {
        subtaskViewModel.updateSubTask(item.copy(isDone = isTaskCompleted))
    }

    private fun addOrRenameDialog(subTask: SubTask?) {
        val title: String
        val ok: String = if (subTask == null) {
            title = getString(R.string.new_task)
            getString(R.string.add)
        } else {
            title = getString(R.string.edit_task)
            getString(R.string.rename)
        }

        val renameBinding = RenameDialogBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(renameBinding.root)
            .setTitle(title)
            .setPositiveButton(ok) { dialog, _ ->
                val rename = renameBinding.renameText.text.toString()
                if (rename.isNotEmpty()) {
                    //rename title
                    if (subTask != null) {
                        subtaskViewModel.updateSubTask(subTask.copy(title = rename))
                    } else {
                        val tempTask = SubTask(task.id, rename, false, 0)
                        subtaskViewModel.insertSubTask(tempTask)
                        val list = subtaskAdapter.currentList.plus(tempTask)
                        taskViewModel.update(task.copy(subtask = list))
                    }
                    dialog.dismiss()
                } else {
                    AppModule.notifyUser(requireContext(), getString(R.string.please_enter_title))
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeleteDialog(isDone: Boolean, subTask: SubTask) {
        if (isDone) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete_confirm))
                .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                    subtaskViewModel.deleteSubTask(subTask)
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
        } else {
            addOrRenameDialog(subTask)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}