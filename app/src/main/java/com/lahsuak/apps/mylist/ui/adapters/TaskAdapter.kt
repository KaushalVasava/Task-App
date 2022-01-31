package com.lahsuak.apps.mylist.ui.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.databinding.TaskItemBinding
import com.lahsuak.apps.mylist.ui.fragments.ListFragment.Companion.is_in_action_mode
import com.lahsuak.apps.mylist.ui.fragments.ListFragment.Companion.is_select_all
import com.lahsuak.apps.mylist.ui.fragments.ListFragment.Companion.selectedItem
import com.lahsuak.apps.mylist.util.Util

class TaskAdapter(private val context: Context, private val listener: TaskListener) :
    ListAdapter<Task, TaskAdapter.TodoViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

        val currentItem = getItem(position)
        holder.bind(currentItem)

    }

    inner class TodoViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        if (is_in_action_mode) {
                            if (!selectedItem!![position]) {
                                root.strokeWidth = 5
                            } else {
                                root.strokeWidth = 0
                            }
                        }
                        listener.onItemClicked(task, position)
                    }
                }
                checkbox.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        if (!is_in_action_mode) {
                            listener.onCheckBoxClicked(task, checkbox.isChecked)
                        }
                    }
                }
                delete.setOnClickListener {
                    val position = adapterPosition
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        if (!is_in_action_mode) {
                            listener.onDeleteClicked(task)
                        }
                    }
                }
                root.setOnLongClickListener {
                    val position = adapterPosition
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAnyItemLongClicked(position)
                        if (selectedItem == null) {

                        } else {
                            if (selectedItem!![position]) {
                                root.strokeWidth = 5
                            } else {
                                root.strokeWidth = 0
                            }
                        }
//                        return@setOnLongClickListener true
                    }
                    return@setOnLongClickListener true
                }
                copyBtn.setOnClickListener {
                    val position = adapterPosition
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        val text = getItem(position).title
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText("simple text",text)
                        clipboard.setPrimaryClip(clip)
                        Util.notifyUser(context,"Copied")
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                title.text = task.title
                val prefMgr = PreferenceManager.getDefaultSharedPreferences(context)
                val txtSize = prefMgr.getString("font_size","18")!!.toFloat()
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize)
                if (!is_in_action_mode) {
                    root.strokeWidth = 0
                 } else {
                    if (is_select_all) {
                        root.strokeWidth = 5
                        if (selectedItem != null) {
                            selectedItem!![adapterPosition] = true
                        }
                    } else {
                        root.strokeWidth = 0
                        if (selectedItem != null) {
                            selectedItem!![adapterPosition] = false
                        }
                    }
                }

                if (task.isDone) {
                    checkbox.isChecked = true
                    title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    delete.setImageResource(R.drawable.ic_delete)
                } else {
                    checkbox.isChecked = false
                    title.paintFlags = title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    delete.setImageResource(R.drawable.ic_edit)
                }
                if (task.isImp) {
                    isImportant.background = getDrawable(context, R.drawable.background_imp)
                } else {
                    isImportant.background = null
                }
            }
        }
    }

    interface TaskListener {
        fun onItemClicked(task: Task, position: Int)
        fun onDeleteClicked(task: Task)
        fun onCheckBoxClicked(task: Task, taskCompleted: Boolean)
        fun onAnyItemLongClicked(position: Int)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}
