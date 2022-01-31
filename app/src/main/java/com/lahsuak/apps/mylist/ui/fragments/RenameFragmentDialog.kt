package com.lahsuak.apps.mylist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.databinding.FragmentDialogRenameBinding
import com.lahsuak.apps.mylist.ui.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.ui.viewmodel.SubTaskViewModel
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RenameFragmentDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDialogRenameBinding
    private val args: RenameFragmentDialogArgs by navArgs()
    private val model: TaskViewModel by viewModels()
    private val subModel: SubTaskViewModel by viewModels()
    private lateinit var task: Task
    private lateinit var subTask: SubTask

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDialogRenameBinding.inflate(inflater, container, false)
        var isImp = false

        @Suppress("deprecation")
        if (dialog!!.window != null) {
            dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            if (!args.source) {
                if (args.taskId != -1) {
                    task = model.getById(args.taskId)
                    binding.impTask.isChecked = task.isImp
                    binding.renameText.setText(task.title)
                    isImp = task.isImp
                }
            } else {
                if (args.subTaskId != -1) {
                    subTask = subModel.getBySubTaskId(args.subTaskId)
                    binding.impTask.isChecked = subTask.isImportant
                    binding.renameText.setText(subTask.subTitle)
                    isImp = subTask.isImportant
                }//subtask
            }
        }

        if(!args.takTitle.isNullOrEmpty())
            binding.renameText.setText(args.takTitle)
        binding.renameText.requestFocus()
        binding.impTask.setOnCheckedChangeListener { _, isChecked ->
            binding.impTask.isChecked = isChecked
            isImp = isChecked
        }
        binding.saveBtn.setOnClickListener {
            if (args.taskId == -1 && !args.source) {
                if (!binding.renameText.text.isNullOrEmpty()) {
                    task = Task(0, binding.renameText.text.toString(), false, isImp, null)
                    model.insert(task)
                }
            } else if (args.taskId != -1 && args.subTaskId == -1 && args.source) {
                subTask = SubTask(
                    args.taskId,
                    binding.renameText.text.toString(),
                    false,
                    isImp,
                    0
                )
                subModel.insertSubTask(subTask)
                //model.update(task)
            } else {
                if (!binding.renameText.text.isNullOrEmpty()) {
                    if (!args.source) {
                        task.title = binding.renameText.text.toString()
                        task.isImp = isImp
                        model.update(task)

                    } else {
                        subTask.subTitle = binding.renameText.text.toString()
                        subTask.isImportant = isImp
                        subModel.updateSubTask(subTask)
                    }
                }
            }
            dismiss()
        }
        return binding.root
    }

}