package com.lahsuak.apps.mylist.ui.fragments

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lahsuak.apps.mylist.EDIT_TASK
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.databinding.FragmentListBinding
import com.lahsuak.apps.mylist.model.Task
import com.lahsuak.apps.mylist.ui.TodoAdapter
import com.lahsuak.apps.mylist.ui.TodoListener
import com.lahsuak.apps.mylist.ui.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(), TodoListener {
    private lateinit var binding: FragmentListBinding
    private lateinit var navController: NavController
    private val model: TodoViewModel by viewModels()
    private lateinit var adapter: TodoAdapter

    companion object {
        var todoList = mutableListOf<Task>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)
        adapter = TodoAdapter(requireContext(), todoList, this)
        binding.todoRecyclerView.adapter = adapter

        navController = findNavController()
        model.todos.observe(viewLifecycleOwner) {
            val fi  = it
            todoList.clear()
            todoList.addAll(it as MutableList<Task>)
            adapter.notifyDataSetChanged()
        }
        binding.fab.setOnClickListener {
            model.addOrRenameDialog(requireContext(),true,0,layoutInflater)
        }

    }

    override fun onItemClicked(position: Int, todo: Task) {
        val action =
            ListFragmentDirections.actionListFragmentToAddUpdateFragment(
                EDIT_TASK,
                todo.id,
                todo.title
            )
        navController.navigate(action)
    }

    override fun onCheckBoxClicked(
        position: Int,
        todo: Task,
        isChecked: Boolean,
        holder: TodoAdapter.TodoViewHolder
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

    override fun onDeleteClicked(id: Int, isDone: Boolean) {
        model.showDeleteDialog(requireContext(),isDone,id, todoList as ArrayList<Task>)
    }
}