package com.lahsuak.apps.mylist.ui.adapter.viewholder

import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.databinding.TaskItemBinding
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.util.ItemClickedListener

class SubTaskViewHolder(
    private val binding: TaskItemBinding,
    private val listener: ItemClickedListener<SubTask>
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(subTask: SubTask) {
        binding.txtTitle.text = subTask.title
        binding.checkbox.isChecked = subTask.isDone
        binding.txtTitle.paintFlags = if (subTask.isDone) {
            binding.btnDelete.setImageResource(R.drawable.ic_delete)
            binding.txtTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            binding.btnDelete.setImageResource(R.drawable.ic_edit)
            binding.txtTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        binding.btnDelete.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onDeleteClicked(subTask, subTask.isDone, position)
            }
        }
        binding.checkbox.setOnClickListener {
            listener.onCheckBoxClicked(subTask, binding.checkbox.isChecked)
        }
        binding.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClicked(subTask, position)
            }
        }
    }
}