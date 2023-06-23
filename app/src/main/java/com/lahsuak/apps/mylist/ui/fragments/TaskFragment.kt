package com.lahsuak.apps.mylist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lahsuak.apps.mylist.EDIT_TASK
import com.lahsuak.apps.mylist.databinding.FragmentTaskBinding
import com.lahsuak.apps.mylist.model.Task
import com.lahsuak.apps.mylist.ui.adapter.TaskAdapter
import com.lahsuak.apps.mylist.ui.TaskViewModel
import com.lahsuak.apps.mylist.util.ItemClickedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment : Fragment(), ItemClickedListener<Task> {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        taskAdapter = TaskAdapter(this)
        binding.taskRecyclerView.apply {
            setHasFixedSize(true)
            adapter = taskAdapter
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }

        binding.btnAddTask.setOnClickListener {
            viewModel.addOrRenameDialog(requireContext(), null)
        }
    }

    override fun onCheckBoxClicked(item: Task, isTaskCompleted: Boolean) {
        viewModel.update(item.copy(isDone = isTaskCompleted))
    }

    override fun onItemClicked(item: Task, position: Int) {
        val action =
            TaskFragmentDirections.actionTaskFragmentToSubTaskFragment(
                EDIT_TASK,
                item.id,
                item.title
            )
        navController.navigate(action)
    }

    override fun onDeleteClicked(item: Task, isDone: Boolean, position: Int) {
        viewModel.showDeleteDialog(requireContext(), isDone, item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}