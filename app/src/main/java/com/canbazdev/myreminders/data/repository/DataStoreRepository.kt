package com.canbazdev.myreminders.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.canbazdev.myreminders.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/*
*   Created by hamzacanbaz on 27.03.2022
*/

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.USER_PREFERENCES)

@Singleton //single source of truth
class DataStoreRepository @Inject constructor(@ApplicationContext val context: Context) {

    private val IS_NAME_SAVED_FIRST_TIME = booleanPreferencesKey("is_name_saved_first_time")
    private val SAVED_NAME = stringPreferencesKey("saved_name")
    private val WIDGET_ID = intPreferencesKey("widget_id")
    private val TODAY_REMINDERS_COUNT = intPreferencesKey("today_reminders_count")


    val getSavedName: Flow<String> = context.dataStore.data.map {
        it[SAVED_NAME] ?: "test"
    }

    suspend fun setSavedName(newName: String) {
        context.dataStore.edit {

            it[SAVED_NAME] = newName
        }
    }

    val getSavedNameFirstTime: Flow<Boolean> = context.dataStore.data.map {
        it[IS_NAME_SAVED_FIRST_TIME] ?: false
    }

    suspend fun setSavedNameFirstTime(isSaved: Boolean) {
        context.dataStore.edit {
            it[IS_NAME_SAVED_FIRST_TIME] = isSaved

        }
    }


    val getWidgetId: Flow<Int> = context.dataStore.data.map {
        it[WIDGET_ID] ?: 0
    }

    suspend fun setWidgetId(newWidgetId: Int) {
        context.dataStore.edit {
            it[WIDGET_ID] = newWidgetId
        }
    }

    val getTodayReminderCount: Flow<Int> = context.dataStore.data.map {
        it[TODAY_REMINDERS_COUNT] ?: 0
    }

    suspend fun setTodayReminderCount(count: Int) {
        context.dataStore.edit {
            it[TODAY_REMINDERS_COUNT] = count
        }
    }

}
