package com.lahsuak.apps.mylist.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferenceManager"

enum class SortOrder { BY_NAME, BY_DATE }

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.createDataStore("SETTING")

    val preferencesFlow = dataStore.data
        .catch { exception->
            Log.e(TAG, "Error reading preferences",exception)
            if(exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preferences ->
        val sortOrder = SortOrder.valueOf(
            preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
        )
        val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED] ?: false
         FilterPreferences(sortOrder,hideCompleted)
    }
    val preferencesFlow2 = dataStore.data
        .catch { exception->
            Log.e(TAG, "Error reading preferences",exception)
            if(exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }
        .map { preferences ->
        val sortOrder = SortOrder.valueOf(
            preferences[PreferencesKeys.SORT_ORDER2] ?: SortOrder.BY_DATE.name
        )
        val hideCompleted = preferences[PreferencesKeys.HIDE_COMPLETED2] ?: false
         FilterPreferences(sortOrder,hideCompleted)
    }
    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataStore.edit { preferences->
            preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }
    suspend fun updateHideCompleted(hideCompleted: Boolean){
        dataStore.edit { preferences->
            preferences[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }
    suspend fun updateSortOrder2(sortOrder: SortOrder){
        dataStore.edit { preferences->
            preferences[PreferencesKeys.SORT_ORDER2] = sortOrder.name
        }
    }
    suspend fun updateHideCompleted2(hideCompleted: Boolean){
        dataStore.edit { preferences->
            preferences[PreferencesKeys.HIDE_COMPLETED2] = hideCompleted
        }
    }


    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
        val SORT_ORDER2 = preferencesKey<String>("sort_order2")
        val HIDE_COMPLETED2 = preferencesKey<Boolean>("hide_completed2")
    }
}