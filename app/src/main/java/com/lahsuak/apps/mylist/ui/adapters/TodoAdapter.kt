package com.lahsuak.apps.mylist.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.data.model.Task
import java.util.ArrayList

class TodoAdapter(
    private var mContext: Context,
    private var list: List<Task>,
    private var listener: TodoListener
) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<Task>?) {
        list = ArrayList()
        (list as ArrayList<Task>).clear()
        (list as ArrayList<Task>).addAll(newList!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.title.text = list[position].title

        if (list[position].isDone) {
            holder.checkBox.isChecked = true
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_delete)
        }else{
            holder.editOrDeleteBtn.setImageResource(R.drawable.ic_edit)
        }
        holder.editOrDeleteBtn.setOnClickListener { 
            listener.onDeleteClicked(list[position].id,list[position].isDone)
        }
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            listener.onCheckBoxClicked(list,list[position].id,list[position],isChecked,holder)
        }
        holder.itemView.setOnClickListener {
            listener.onItemClicked(
                position,
                list[position]
            )
        }

    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val editOrDeleteBtn: ImageView = itemView.findViewById(R.id.delete)
        val checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkbox)
//        var cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

interface TodoListener {
    fun onItemClicked(position: Int, task: Task,)
    fun onDeleteClicked(id: Int,isDone: Boolean)
    fun onCheckBoxClicked(list: List<Task>,position: Int, task: Task, isChecked: Boolean, holder: TodoAdapter.TodoViewHolder)
}
