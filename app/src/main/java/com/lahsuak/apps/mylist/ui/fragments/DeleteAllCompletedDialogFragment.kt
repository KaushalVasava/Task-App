package com.lahsuak.apps.mylist.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.lahsuak.apps.mylist.ui.viewmodel.DeleteAllCompletedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment:DialogFragment() {

   // private val args:DeleteAllCompletedDialogFragmentArgs by navArgs()
    private val viewModel: DeleteAllCompletedViewModel by viewModels()
  //  private val source = args.sourceID
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you want to delete all completed tasks?")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Delete"){_,_->
                //if(source == 0) {
                    viewModel.onConfirmClick()
                //}
//                else if(source==SourceId.FROM_SUBTASK.ordinal){
//                    viewModel.onConfirmClick2()
//                }else if(source == SourceId.FROM_TASK_DELETE.ordinal){
//                    //viewModel
//                    viewModel.deleteAllTasks()
//                }
            }
            .create()
}