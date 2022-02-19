package com.canbazdev.myreminders.repository

import androidx.lifecycle.LiveData
import com.canbazdev.myreminders.data.local.dao.ReminderDao
import com.canbazdev.myreminders.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {
    fun getAllReminders(): LiveData<List<Reminder>> = reminderDao.getAllReminders()
    fun getTodaysAllReminders(currentDate: String): LiveData<List<Reminder>> =
        reminderDao.getTodaysAllReminders(currentDate)

    suspend fun insertReminder(reminder: Reminder) = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)
    suspend fun deleteAllReminders() = reminderDao.deleteAll()
}