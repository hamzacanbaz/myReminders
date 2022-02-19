package com.canbazdev.myreminders.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.canbazdev.myreminders.model.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder_table ORDER BY text ASC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder_table WHERE date =:currentDate")
    fun getTodaysAllReminders(currentDate: String): LiveData<List<Reminder>>

    // TODO Get closest reminder

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM reminder_table")
    suspend fun deleteAll()


}