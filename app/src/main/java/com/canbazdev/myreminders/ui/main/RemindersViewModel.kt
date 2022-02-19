package com.canbazdev.myreminders.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.util.Event
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@DelicateCoroutinesApi
class RemindersViewModel(
    private val repository: ReminderRepository,
    private val sharedPrefRepository: SharedPrefRepository
) : ViewModel() {

    private val _statusMessage = MutableLiveData<Event<String>>()

    val toastMessage: LiveData<Event<String>>
        get() = _statusMessage


    val reminderList: LiveData<List<Reminder>> = getAllReminders()
    val todaysReminderList: LiveData<List<Reminder>> = getTodaysAllReminders(
        getCurrentDateWithNormalFormat()
    )

    //    private val mShowProgressBarUserInfo: MutableLiveData<Boolean> = MutableLiveData(true)
//    val showProgressBarUserInfo: LiveData<Boolean> get() = mShowProgressBarUserInfo
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(true)


    init {
        isLoading.value = true
        if (!getSavedDataFirstTime()) {
            GlobalScope.launch {
                repeat(3) {
                    val reminder = Reminder(
                        it.toLong(),
                        "Yonunu bilmeyen gemiye hicbir ruzgar yardim etmez."
                    )
                    insertReminder(reminder)
                }

            }
            setSavedDataFirstTime(true)
        }
    }

    fun insertReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.insertReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
        _statusMessage.value = Event("Reminder updated")
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun deleteAllReminders() {
        viewModelScope.launch {
            repository.deleteAllReminders()
        }
    }

    private fun getAllReminders(): LiveData<List<Reminder>> {
//        mShowProgressBarUserInfo.postValue(false)
        return repository.getAllReminders()
    }

    private fun getTodaysAllReminders(currentDate: String): LiveData<List<Reminder>> {
//        mShowProgressBarUserInfo.postValue(false)
        return repository.getTodaysAllReminders(currentDate)
    }

    private fun getSavedDataFirstTime(): Boolean {
        return sharedPrefRepository.getDataFirstTime()
    }

    private fun setSavedDataFirstTime(savedFirstTime: Boolean) {
        sharedPrefRepository.setDataFirstTime(savedFirstTime)
    }

    fun splitDateStringIntoString(dateString: String): List<String> {
        // Feb 21, 2022
        return dateString.split(" ")
    }

    fun getReminderDateFromFormatter(dateText: String, formatPattern: String): Long {
        val simpleDateFormat = SimpleDateFormat(formatPattern, Locale.US)
        val endDate: Date = simpleDateFormat.parse(dateText)!!
        return endDate.time

    }

    fun getFormattedReminderDate(
        dateText: String,
        inputFormatPattern: String,
        outputFormatPattern: String
    ): String {
        val inputFormat = SimpleDateFormat(inputFormatPattern, Locale.US)
        val outputFormat = SimpleDateFormat(outputFormatPattern, Locale.US)
        var date: Date? = null
        var outputDate: String? = " "

        try {
            date = inputFormat.parse(dateText)
            outputDate = outputFormat.format(date!!)
            Log.e("Log ", "str $outputDate")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDate!!
    }


    fun splitTimeIntoHourAndMinute(timeText: String): Pair<Int, Int> {
        val splittedTimeList = timeText.split(":")
        val hour = splittedTimeList[0].trim().toInt()
        val minute = splittedTimeList[1].trim().toInt()
        return Pair(hour, minute)

    }

    fun getCurrentDateWithNormalFormat(): String {

        val dateFormat =
            SimpleDateFormat("MMM dd, yyyy", Locale.US)
        return dateFormat.format(Date())

    }

}