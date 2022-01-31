package com.lahsuak.apps.mylist.ui.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.databinding.TaskItemBinding
import com.lahsuak.apps.mylist.ui.fragments.AddUpdateFragment.Companion.is_in_action_mode2
import com.lahsuak.apps.mylist.ui.fragments.AddUpdateFragment.Companion.is_select_all2
import com.lahsuak.apps.mylist.ui.fragments.AddUpdateFragment.Companion.selectedItem2
import com.lahsuak.apps.mylist.util.Util

class SubTaskAdapter(private val context: Context, private val listener: SubTaskListener) :
    ListAdapter<SubTask, SubTaskAdapter.SubTaskViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class SubTaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        if (is_in_action_mode2) {
                            if (!selectedItem2!![position]) {
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
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        if (!is_in_action_mode2) {
                            listener.onCheckBoxClicked(task, checkbox.isChecked)
                        }
                    }
                }
                delete.setOnClickListener {
                    val position = adapterPosition
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        val subTask = getItem(position)
                        if (!is_in_action_mode2)
                            listener.onDeleteClicked(subTask)
                    }
                }

                root.setOnLongClickListener {
                    val position = adapterPosition
                    //checking for -1 index when task will be deleted
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAnyItemLongClicked(position)
                        if (selectedItem2 == null) {
                        } else {
                            if (selectedItem2!![position]) {
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
                        val text = getItem(position).subTitle
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText("simple text", text)
                        clipboard.setPrimaryClip(clip)
                        Util.notifyUser(context, "Copied")
                    }
                }
            }
        }

        fun bind(subTask: SubTask) {
            binding.apply {
                title.text = subTask.subTitle
                val prefMgr = PreferenceManager.getDefaultSharedPreferences(context)
                val txtSize = prefMgr.getString("font_size","18")!!.toFloat()
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize)
                if (!is_in_action_mode2) {
                    root.strokeWidth = 0
                } else {
                    if (is_select_all2) {
                        root.strokeWidth = 5
                        if (selectedItem2 != null) {
                            selectedItem2!![adapterPosition] = true
                        }
                    } else {
                        root.strokeWidth = 0
                        if (selectedItem2 != null) {
                            selectedItem2!![adapterPosition] = false
                        }
                    }
                }
                if (subTask.isDone) {
                    checkbox.isChecked = true
                    title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    delete.setImageResource(R.drawable.ic_delete)
                } else {
                    checkbox.isChecked = false
                    title.paintFlags = title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    delete.setImageResource(R.drawable.ic_edit)
                }
                if (subTask.isImportant) {
                    isImportant.background = AppCompatResources.getDrawable(
                        context,
                        R.drawable.background_imp
                    )
                } else {
                    isImportant.background = null
                }
            }
        }
    }


    interface SubTaskListener {
        fun onItemClicked(subTask: SubTask, position: Int)
        fun onDeleteClicked(subTask: SubTask)
        fun onCheckBoxClicked(subTask: SubTask, taskCompleted: Boolean)
        fun onAnyItemLongClicked(position: Int)
    }

    class DiffCallback : DiffUtil.ItemCallback<SubTask>() {
        override fun areItemsTheSame(oldItem: SubTask, newItem: SubTask) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SubTask, newItem: SubTask) =
            oldItem == newItem
    }
}
