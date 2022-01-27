package com.lahsuak.apps.mylist.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.lahsuak.apps.mylist.ui.viewmodel.DeleteAllCompletedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment2:DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you want to delete all completed tasks?")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Delete"){_,_->
                viewModel.onConfirmClick2()
            }
            .create()
}