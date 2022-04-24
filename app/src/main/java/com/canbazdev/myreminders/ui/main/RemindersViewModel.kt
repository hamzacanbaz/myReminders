package com.canbazdev.myreminders.ui.main

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canbazdev.myreminders.adapter.RemindersAdapter
import com.canbazdev.myreminders.data.model.Reminder
import com.canbazdev.myreminders.data.repository.DataStoreRepository
import com.canbazdev.myreminders.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val repository: ReminderRepository
) : ViewModel(), RemindersAdapter.OnItemClickedListener {


    private val _savedName = MutableLiveData<String>()
    val savedName: LiveData<String>
        get() = _savedName

    val leftTime: MutableLiveData<String> = MutableLiveData()

    private val _reminderList: MutableLiveData<List<Reminder>> = MutableLiveData()
    val reminderList: LiveData<List<Reminder>> = _reminderList

//    private val _todaysReminderList: MutableLiveData<List<Reminder>> = MutableLiveData()
//    val todaysReminderList: LiveData<List<Reminder>> = _todaysReminderList

    private val _closestReminderTitle: MutableLiveData<String> = MutableLiveData("You are free")
    val closestReminderTitle: LiveData<String> = _closestReminderTitle

    private val _closestReminderTime: MutableLiveData<String> = MutableLiveData("")
    val closestReminderTime: LiveData<String> = _closestReminderTime

    private val _goToDetail: MutableLiveData<Boolean> = MutableLiveData(false)
    val goToDetail: LiveData<Boolean> = _goToDetail

    private val _currentReminder: MutableLiveData<Reminder> = MutableLiveData()
    val currentReminder: LiveData<Reminder> = _currentReminder

    fun setGoToDetail() {
        _goToDetail.postValue(false)
    }

//    private val _closestReminderToday: MutableLiveData<Reminder> = MutableLiveData()
//    val closestReminderToday: LiveData<Reminder> = _closestReminderToday

    private val _todaysRemindersCount = MutableLiveData(0)
    val todaysRemindersCount: LiveData<Int>
        get() = _todaysRemindersCount

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _rvVisibility = MutableLiveData(true)
    val rvVisibility: LiveData<Boolean> = _rvVisibility

    private val _noDataFoundVisibility = MutableLiveData(true)
    val noDataFoundVisibility: LiveData<Boolean> = _noDataFoundVisibility

    private val _widgetId = MutableLiveData(0)
    val widgetId: LiveData<Int> = _widgetId

    init {
        getAllReminders()
        getTodaysAllReminders()
        getNameFirstTime()
        getClosestReminderToday()
        //getTodaysReminderCount()

    }


    private fun setIsLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun insertReminder(reminder: Reminder) {
        val formattedReminder = reminder.copy(date = formatDate(reminder.date))
        viewModelScope.launch {
            repository.insertReminder(formattedReminder)
        }
        getClosestReminderToday()
        getTodaysAllReminders()
        getAllReminders()
    }

    private fun formatDate(date: String): String {
        val split = date.split("/")
        var newFormattedDateString = ""
        val day = split[0].toInt()
        val month = split[1].toInt()
        val year = split[2].toInt()

        newFormattedDateString += if (day < 10) "0$day/" else "$day/"
        newFormattedDateString += if (month < 10) "0$month/" else "$month/"

        newFormattedDateString += "$year"
        return newFormattedDateString

    }


    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
        getClosestReminderToday()
        getTodaysAllReminders()
        getAllReminders()
    }

//    fun deleteAllReminders() {
//        viewModelScope.launch {
//            repository.deleteAllReminders()
//        }
//    }

    private fun getAllReminders() {
        viewModelScope.launch {
            val result = repository.getAllReminders()
            if (result.isNotEmpty()) {
                _reminderList.postValue(result)
                _rvVisibility.value = true
                _noDataFoundVisibility.value = false
                setIsLoading(false)
            } else {
                _rvVisibility.value = false
                _noDataFoundVisibility.value = true
                setIsLoading(false)
            }

        }
        return
    }

//    fun getWidgetId() {
//        viewModelScope.launch {
//            dataStoreRepository.getWidgetId.collect {
//                _widgetId.value = it
//            }
//        }
//    }

    private fun getTodaysAllReminders() {
        viewModelScope.launch {
            val result = repository.getTodaysAllReminders(getCurrentDateWithNormalFormat())
//            _todaysReminderList.value = result
            _todaysRemindersCount.value = result.size

        }
    }

    private fun getClosestReminderToday() {
        viewModelScope.launch {
            val closestReminder: Reminder? = repository.getClosestReminderToday(
                getCurrentDateWithNormalFormat(),
                getCurrentTimeWithNormalFormat()
            )

            if (closestReminder != null) {
                val closestReminderTitle =
                    closestReminder.title.lowercase(Locale.getDefault()).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                        else it.toString()
                    }
                _closestReminderTitle.value = closestReminderTitle
                _closestReminderTime.value = closestReminder.time
            } else {
                _closestReminderTitle.value = "You are free"
                _closestReminderTime.value = ""
                getTodaysAllReminders()

            }
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

    private fun getCurrentDateWithNormalFormat(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
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

    private fun twoDigitString(number: Long): String {
        if (number == 0L) {
            return "00"
        }
        return if (number / 10 == 0L) {
            "0$number"
        } else number.toString()
    }

    override fun onItemClicked(position: Int, reminder: Reminder) {
        _currentReminder.value = reminder
        _goToDetail.value = true
    }


}