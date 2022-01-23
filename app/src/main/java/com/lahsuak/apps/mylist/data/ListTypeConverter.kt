package com.lahsuak.apps.mylist.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lahsuak.apps.mylist.model.SubTask
import java.util.*
import java.util.concurrent.Flow

class ListTypeConverter {
    var gson: Gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String?): List<SubTask>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType = object : TypeToken<List<SubTask>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<SubTask>?): String? {
        return gson.toJson(someObjects)
    }
}