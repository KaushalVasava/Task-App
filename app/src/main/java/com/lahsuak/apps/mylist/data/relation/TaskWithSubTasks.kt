package com.lahsuak.apps.mylist.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.lahsuak.apps.mylist.model.SubTask
import com.lahsuak.apps.mylist.model.Task
import kotlinx.coroutines.flow.Flow

data class TaskWithSubTasks(
    @Embedded val task:Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val subTasks : List<SubTask>
)