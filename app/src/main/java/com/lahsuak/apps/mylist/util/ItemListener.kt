package com.lahsuak.apps.mylist.util

interface ItemClickedListener<in ITEM : Any> {
    fun onItemClicked(item: ITEM, position: Int)
    fun onDeleteClicked(item: ITEM, isDone: Boolean, position: Int)
    fun onCheckBoxClicked(item: ITEM, isTaskCompleted: Boolean)
}


