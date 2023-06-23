package com.lahsuak.apps.mylist.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lahsuak.apps.mylist.databinding.TaskItemBinding
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.ui.adapter.viewholder.SubTaskViewHolder
import com.lahsuak.apps.mylist.util.ItemClickedListener

class SubTaskAdapter(
    private val listener: ItemClickedListener<SubTask>
) : ListAdapter<SubTask, SubTaskViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    class DiffCallback : DiffUtil.ItemCallback<SubTask>() {
        override fun areItemsTheSame(oldItem: SubTask, newItem: SubTask) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SubTask, newItem: SubTask) =
            oldItem == newItem
    }
}


