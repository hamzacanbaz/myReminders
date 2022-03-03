package com.canbazdev.myreminders.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.util.Constants.DATA_SAVED_FIRST_TIME
import com.canbazdev.myreminders.util.Constants.NAME_FIRST_TIME
import com.canbazdev.myreminders.util.Constants.NAME_SAVED_FIRST_TIME
import com.canbazdev.myreminders.util.Constants.PREFERENCE_NAME
import com.canbazdev.myreminders.util.Constants.WIDGET_ID

class SharedPrefRepository(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    private val editor = pref.edit()

    private fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.getLong() = pref.getLong(this, 0)

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.getBoolean() = pref.getBoolean(this, false)

    fun setDataFirstTime(savedFirstTime: Boolean) {
        DATA_SAVED_FIRST_TIME.put(savedFirstTime)
    }

    fun getDataFirstTime(): Boolean = DATA_SAVED_FIRST_TIME.getBoolean()

    fun setNameFirstTime(savedFirstTime: Boolean) {
        NAME_SAVED_FIRST_TIME.put(savedFirstTime)
    }

    fun getNameFirstTime(): Boolean = NAME_SAVED_FIRST_TIME.getBoolean()

    fun setNameFirstText(savedName: String) {
        NAME_FIRST_TIME.put(savedName)
    }

    fun getNameFirstText(): String = NAME_FIRST_TIME.getString()

    fun setTodaysReminders(todaysAllReminders: LiveData<List<Reminder>>) {

    }

    fun setWidgetId(id: Int) {
        return WIDGET_ID.put(id)
    }


    fun getWidgetId(): Int {
        return WIDGET_ID.getInt()
    }

}