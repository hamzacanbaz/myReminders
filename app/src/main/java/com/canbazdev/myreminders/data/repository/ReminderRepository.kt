package com.canbazdev.myreminders.data.repository

import com.canbazdev.myreminders.data.local.dao.ReminderDao
import com.canbazdev.myreminders.data.model.Reminder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val reminderDao: ReminderDao) {
    suspend fun getAllReminders(): List<Reminder> = reminderDao.getAllReminders()

    suspend fun getTodaysAllReminders(currentDate: String): List<Reminder> =
        reminderDao.getTodaysAllReminders(currentDate)

    fun getTodaysAllRemindersForWidget(currentDate: String): List<Reminder> =
        reminderDao.getTodaysAllRemindersForWidget(currentDate)

    suspend fun getClosestReminderToday(currentDate: String, currentTime: String): Reminder? =
        reminderDao.getClosestReminderToday(currentDate, currentTime)

    suspend fun insertReminder(reminder: Reminder) = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)
    suspend fun deleteAllReminders() = reminderDao.deleteAll()
}