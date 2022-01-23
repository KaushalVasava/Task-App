package com.lahsuak.apps.mylist.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.databinding.FragmentListBinding
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.ui.adapters.TodoAdapter
import com.lahsuak.apps.mylist.ui.adapters.TodoListener
import com.lahsuak.apps.mylist.ui.viewmodel.TodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ListFragment : Fragment(), TodoListener, SearchView.OnQueryTextListener {
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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListBinding.bind(view)
        adapter = TodoAdapter(requireContext(), todoList, this)
        binding.todoRecyclerView.adapter = adapter

        navController = findNavController()
        model.todos.observe(viewLifecycleOwner) {
            todoList.clear()
            todoList.addAll(it as MutableList<Task>)
            adapter.notifyDataSetChanged()
        }
        binding.fab.setOnClickListener {
            model.addOrRenameDialog(requireContext(), true, 0, layoutInflater)
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
                model.getCompletedTask(item.isChecked).observe(viewLifecycleOwner) {
                    todoList.clear()
                    todoList.addAll(it as MutableList<Task>)
                    adapter.notifyDataSetChanged()
                }
            }
            R.id.setting -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClicked(position: Int, task: Task) {
        val action =
            ListFragmentDirections.actionListFragmentToAddUpdateFragment(
                task.id,
                task.title
            )
        navController.navigate(action)
    }

    override fun onCheckBoxClicked(list: List<Task>,
        position: Int,
        task: Task,
        isChecked: Boolean,
        holder: TodoAdapter.TodoViewHolder
    ) {
        if (!isChecked) {
            holder.checkBox.isChecked = false
            task.isDone = false
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_edit)
            model.update(task)
            //list[position].isDone= false
        } else {
            holder.checkBox.isChecked = true
            task.isDone = true
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_delete)
            model.update(task)
            //list[position].isDone= true
        }
    }

    override fun onDeleteClicked(id: Int, isDone: Boolean) {
        model.showDeleteDialog(requireContext(), isDone, id, todoList as ArrayList<Task>)
       // adapter.updateList(todoList as java.util.ArrayList<Task>)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val input = newText!!.lowercase(Locale.getDefault())
        val myFiles = ArrayList<Task>()
        for (item in todoList) {
            if (item.title.lowercase(Locale.getDefault()).contains(input)) {
                myFiles.add(item)
            }
        }
        adapter.updateList(myFiles)

        return true
    }
}