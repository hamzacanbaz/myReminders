package com.canbazdev.myreminders.repository

import androidx.lifecycle.LiveData
import com.canbazdev.myreminders.data.local.dao.ReminderDao
import com.canbazdev.myreminders.model.Reminder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val reminderDao: ReminderDao) {
    fun getAllReminders(): LiveData<List<Reminder>> = reminderDao.getAllReminders()

    fun getTodaysAllReminders(currentDate: String): LiveData<List<Reminder>> =
        reminderDao.getTodaysAllReminders(currentDate)

    fun getTodaysAllRemindersForWidget(currentDate: String): List<Reminder> =
        reminderDao.getTodaysAllRemindersForWidget(currentDate)

    fun getClosestReminderToday(currentDate: String, currentTime: String): LiveData<Reminder> =
        reminderDao.getClosestReminderToday(currentDate, currentTime)

    suspend fun insertReminder(reminder: Reminder) = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)
    suspend fun deleteAllReminders() = reminderDao.deleteAll()
}