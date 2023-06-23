package com.lahsuak.apps.mylist.ui.adapter.viewholder

import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.databinding.TaskItemBinding
import com.lahsuak.apps.mylist.model.Task
import com.lahsuak.apps.mylist.util.ItemClickedListener

class TaskViewHolder(
    private val binding: TaskItemBinding,
    private val listener: ItemClickedListener<Task>
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(task: Task) {
        binding.txtTitle.text = task.title
        binding.checkbox.isChecked = task.isDone
        binding.txtTitle.paintFlags = if (task.isDone) {
            binding.btnDelete.setImageResource(R.drawable.ic_delete)
            binding.txtTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.btnDelete.setImageResource(R.drawable.ic_edit)
            binding.txtTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        binding.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClicked(task, position)
            }
        }
        binding.btnDelete.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onDeleteClicked(task, task.isDone, position)
            }
        }
        binding.checkbox.setOnClickListener {
            listener.onCheckBoxClicked(task, binding.checkbox.isChecked)
        }
    }
}