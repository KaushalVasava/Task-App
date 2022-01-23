package com.lahsuak.apps.mylist.ui

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.model.SubTask

class SubTaskAdapter(
    var mContext: Context,
    var list: List<SubTask>,
    var listener: SubTaskListener
) :
    RecyclerView.Adapter<SubTaskAdapter.SubTaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item, parent, false)
        return SubTaskViewHolder(view)
    }


    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
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
            listener.onCheckBoxClicked(list[position].id,list[position],isChecked,holder)
        }
        holder.itemView.setOnClickListener {
            listener.onItemClicked(
                position,
                list[position]
            )
        }

    }

    class SubTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val editOrDeleteBtn: ImageView = itemView.findViewById(R.id.delete)
        val checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkbox)
//        var cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

interface SubTaskListener {
    fun onItemClicked(position: Int, todo: SubTask)
    fun onDeleteClicked(id: Int,isDone: Boolean)
    fun onCheckBoxClicked(position: Int,todo: SubTask,isChecked: Boolean,holder: SubTaskAdapter.SubTaskViewHolder)
}
