package com.canbazdev.myreminders.repository

import android.content.Context
import android.content.SharedPreferences
import com.canbazdev.myreminders.util.Constants.DATA_SAVED_FIRST_TIME
import com.canbazdev.myreminders.util.Constants.PREFERENCE_NAME

class SharedPrefRepository(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

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

    fun setDataFirstTime(savedFirstTime: Boolean){
        DATA_SAVED_FIRST_TIME.put(savedFirstTime)
    }
    fun getDataFirstTime(): Boolean = DATA_SAVED_FIRST_TIME.getBoolean()

//    private val gson = Gson()

}