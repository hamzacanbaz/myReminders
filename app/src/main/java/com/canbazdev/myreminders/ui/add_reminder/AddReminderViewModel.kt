package com.canbazdev.myreminders.ui.add_reminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.model.Reminder
import com.canbazdev.myreminders.data.repository.ReminderRepository
import com.canbazdev.myreminders.util.enum.Categories
import com.canbazdev.myreminders.util.enum.Months
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/*
*   Created by hamzacanbaz on 27.04.2022
*/
@HiltViewModel
class AddReminderViewModel @Inject constructor(
    application: Application,
    private val repository: ReminderRepository
) : AndroidViewModel(application) {


    private val _currentTime = MutableLiveData<Calendar>()
    val currentTime: LiveData<Calendar> = _currentTime

    private val _currentYear = MutableLiveData<Int>()
    val currentYear: LiveData<Int> = _currentYear

    private val _currentMonth = MutableLiveData<Int>()
    val currentMonth: LiveData<Int> = _currentMonth

    private val _currentDay = MutableLiveData<Int>()
    val currentDay: LiveData<Int> = _currentDay

    private val _showDay = MutableLiveData<String>()
    val showDay: LiveData<String> = _showDay

    private val _showMonth = MutableLiveData<String>()
    val showMonth: LiveData<String> = _showMonth

    private val _displayedHour = MutableLiveData("00")
    val displayedHour: LiveData<String> = _displayedHour

    private val _displayedMinutes = MutableLiveData("00")
    val displayedMinutes: LiveData<String> = _displayedMinutes

    private val _pickedEventTime = MutableLiveData("23")
    val pickedEventTime: LiveData<String> = _pickedEventTime

    private val _pickedEventDate = MutableLiveData<String>()
    val pickedEventDate: LiveData<String> = _pickedEventDate

    private val _category = MutableLiveData(Categories.OTHER.ordinal)
    val category: LiveData<Int> = _category

    private val _reminderTitle = MutableLiveData("")
    private val reminderTitle: LiveData<String> = _reminderTitle


    init {

        _currentTime.value = Calendar.getInstance()
        _currentYear.value = currentTime.value?.get(Calendar.YEAR)
        _currentMonth.value = currentTime.value?.get(Calendar.MONTH)
        _currentDay.value = currentTime.value?.get(Calendar.DAY_OF_MONTH)
//        _initialDate.value = "$currentDay/$currentMonth/$currentYear"


        _pickedEventDate.value =
            currentTime.value?.get(Calendar.DAY_OF_MONTH)
                .toString() + "/" + (currentTime.value?.get(
                Calendar.MONTH
            )?.plus(1)).toString() + "/" + currentTime.value?.get(Calendar.YEAR)


        setPickedEventTime(
            currentTime.value?.get(Calendar.HOUR_OF_DAY).toString() + ":" + currentTime.value?.get(
                Calendar.MINUTE
            ).toString()

        )
        showDate()

    }

    fun setCategoryNumber(ordinal: Int) {
        _category.value = ordinal
    }

    fun setReminderTitle(newTitle: String) {
        _reminderTitle.value = newTitle
    }

    fun showDate() {
        val split = pickedEventDate.value?.split("/")
        var day = split?.get(0)
        val month = split?.get(1)?.toInt()
        val year = split?.get(2)

        if (day != null) {
            day = if (day.length < 2) "0$day" else day
        }
        val monthText: String = Months.values()[month?.minus(1)!!].toString()

        _showDay.value = day!!
        _showMonth.value =
            String.format(
                getApplication<Application>().applicationContext.resources.getString(R.string.text_view_month_and_year),
                monthText,
                year
            )

    }

    fun setPickedEventTime(newTime: String) {
        _pickedEventTime.value = formatTime(newTime)
        _displayedHour.value = formatTime(newTime).trim().split(":")[0].trim()
        _displayedMinutes.value = formatTime(newTime).trim().split(":")[1].trim()

    }

    fun setPickedEventDate(date: String) {
        _pickedEventDate.value = formatDate(date)
    }

    fun insertReminder() {
        val reminder = Reminder(
            title = reminderTitle.value!!,
            date = formatDate(pickedEventDate.value!!),
            time = formatTime(pickedEventTime.value!!),
            category = category.value!!
        )
        viewModelScope.launch {
            repository.insertReminder(reminder)
        }
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

    private fun formatTime(time: String): String {
        val split = time.split(":")
        var hours = split[0]
        var minutes = split[1]
        if (hours.toInt() < 10 && hours.length == 1) hours = "0$hours"
        if (minutes.toInt() < 10 && minutes.length == 1) minutes = "0$minutes"
        return "$hours:$minutes"
    }


//    private fun twoDigitString(number: String): String {
//        if (number.toLong() == 0L) {
//            return "00"
//        }
//        return if (number.toLong() / 10 == 0L) {
//            "0$number"
//        } else number
//    }


}