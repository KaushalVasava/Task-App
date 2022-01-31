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
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShortcutFragmentDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentDialogRenameBinding
    private val args: ShortcutFragmentDialogArgs by navArgs()
    private val model: TaskViewModel by viewModels()
    private lateinit var task: Task

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDialogRenameBinding.inflate(inflater, container, false)

        @Suppress("deprecation")
        if (dialog!!.window != null) {
            dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            if (args.taskId != -1)
                task = model.getById(args.taskId)
        }

        var isImp = false
        binding.renameText.requestFocus()
        binding.impTask.setOnCheckedChangeListener { _, isChecked ->
            binding.impTask.isChecked = isChecked
            isImp = isChecked
        }
        binding.saveBtn.setOnClickListener {
            if (args.taskId == -1) {
                if (!binding.renameText.text.isNullOrEmpty()) {
                    val task = Task(0, binding.renameText.text.toString(), false, isImp, null)
                    model.insert(task)
                }
            } else {
                if (!binding.renameText.text.isNullOrEmpty()) {
                    task.title = binding.renameText.text.toString()
                    if (isImp)
                        task.isImp = isImp
                    model.update(task)
                }
            }
            dismiss()
        }
        return binding.root
    }
}