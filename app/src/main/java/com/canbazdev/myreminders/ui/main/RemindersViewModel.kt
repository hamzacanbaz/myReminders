package com.canbazdev.myreminders.ui.main

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.repository.DataStoreRepository
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

// binding adap. viewmodelden fragmentta göster.
// vm. bir fonk. b.a. ile tetikle
// text içine ne giderse gitsin b.a'de upper case'e dönüştür oradan fragment
// sharedviewmodel bindingadapter!!!!!!!
// activityviewmodel navgraphviewmodel androidviewmodel!!!!!!!!!!!!

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val repository: ReminderRepository
) : ViewModel() {

    private val _statusMessage = MutableLiveData<Event<String>>()

    val toastMessage: LiveData<Event<String>>
        get() = _statusMessage

    private val _savedName = MutableLiveData<String>()
    val savedName: LiveData<String>
        get() = _savedName

    val leftTime: MutableLiveData<String> = MutableLiveData()


    val reminderList: LiveData<List<Reminder>> = getAllReminders()
    val todaysReminderList: LiveData<List<Reminder>> = getTodaysAllReminders(
        getCurrentDateWithNormalFormat()
    )
    val closestReminderToday: LiveData<Reminder> =
        MutableLiveData()

    private val _todaysRemindersCount = MutableLiveData<Int>()
    val todaysRemindersCount: LiveData<Int>
        get() = _todaysRemindersCount


    //    var getSavedNameFirstTime: Boolean = false
    private val _getSavedNameFirstTime = MutableLiveData<Boolean>()
    val getSavedNameFirstTime: LiveData<Boolean>
        get() = _getSavedNameFirstTime


    //    private val mShowProgressBarUserInfo: MutableLiveData<Boolean> = MutableLiveData(true)
    //    val showProgressBarUserInfo: LiveData<Boolean> get() = mShowProgressBarUserInfo
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)


    init {
        getNameFirstTime()
        getSavedNameFirstTime()
        getTodaysReminderCount()

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
        return repository.getTodaysAllReminders(currentDate)
    }

    private fun getClosestReminderToday(
        currentDate: String,
        currentTime: String
    ): LiveData<Reminder> {
        return repository.getClosestReminderToday(currentDate, currentTime)
    }


    fun getSavedNameFirstTime() {
        viewModelScope.launch {
            dataStoreRepository.getSavedNameFirstTime.collect {
                println("saved $it")
                _getSavedNameFirstTime.value = it
            }
            while (true) {
                delay(30000L)
                dataStoreRepository.setSavedNameFirstTime(true)
            }
        }


    }

    fun setSavedNameFirstTime(savedFirstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setSavedNameFirstTime(savedFirstTime)
        }
    }

    fun setNameFirstTime(nameText: String) {
        viewModelScope.launch {
            dataStoreRepository.setSavedName(nameText)
        }
    }

    fun getNameFirstTime() {
        viewModelScope.launch {
            dataStoreRepository.getSavedName.collectLatest {
                _savedName.value = it

            }

        }
    }


    fun setTodaysReminderCount(count: Int) {
        viewModelScope.launch {
            dataStoreRepository.setTodayReminderCount(count)
        }
    }

    fun getTodaysReminderCount() {
        viewModelScope.launch {
            dataStoreRepository.getTodayReminderCount.collectLatest {
                _todaysRemindersCount.value = it

            }
        }
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
        val date: Date?
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
        val splitTimeList = timeText.split(":")
        val hour = splitTimeList[0].trim().toInt()
        val minute = splitTimeList[1].trim().toInt()
        return Pair(hour, minute)
    }

    private fun getCurrentDateWithNormalFormat(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
//        println(dateFormat.format(Date()).toString())
        return dateFormat.format(Date())
    }

    private fun getCurrentTimeWithNormalFormat(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dtf = DateTimeFormatter.ofPattern("HH:mm")
            val now: LocalDateTime = LocalDateTime.now()
            dtf.format(now)
        } else {
            val formatter = SimpleDateFormat("HH:mm", Locale.US)
            val date = Date()
            formatter.format(date)
        }

    }

    fun formatMilliSecondsToTime(milliseconds: Long): String {
        var dates = ""
        val seconds = (milliseconds / 1000).toInt() % 60
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60)).toInt() % 24
        val days = (milliseconds / (1000 * 60 * 60 * 24)).toInt()
        if (days > 0) dates += "$days Days "
        if (days > 0 || hours > 0) dates += twoDigitString(hours.toLong()) + " Hrs "
        if (minutes >= 0) dates += twoDigitString(minutes.toLong()) + " Mins " + twoDigitString(
            seconds.toLong()
        ) + " Secs"
        println("date$dates")
        leftTime.value = dates
        return dates
    }

    private fun twoDigitString(number: Long): String? {
        if (number == 0L) {
            return "00"
        }
        return if (number / 10 == 0L) {
            "0$number"
        } else number.toString()
    }


}