package com.canbazdev.myreminders.ui.detail_reminder

import android.app.Application
import android.os.CountDownTimer
import android.text.Editable
import android.util.Log
import androidx.lifecycle.*
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.model.Reminder
import com.canbazdev.myreminders.data.repository.ReminderRepository
import com.canbazdev.myreminders.util.enum.Categories
import com.canbazdev.myreminders.util.enum.Months
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@HiltViewModel
class DetailReminderViewModel @Inject constructor(
    application: Application,
    private val repository: ReminderRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val _statusMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _statusMessage

    private val _isEventEnded = MutableLiveData(false)
    val isEventEnded: LiveData<Boolean> = _isEventEnded

    val leftTime: MutableLiveData<String> = MutableLiveData()

    private val _pickedDate = MutableLiveData("01/01/2022")
    private val pickedDate: LiveData<String> = _pickedDate

    private val _pickedTime = MutableLiveData("")
    private val pickedTime: LiveData<String> = _pickedTime

    private val _displayedHour = MutableLiveData("00")
    val displayedHour: LiveData<String> = _displayedHour

    private val _displayedMinutes = MutableLiveData("00")
    val displayedMinutes: LiveData<String> = _displayedMinutes

    val date = Date().time

    private val _millisecondsBetweenReminderAndNow = MutableLiveData(0L)
    private val millisecondsBetweenReminderAndNow: LiveData<Long> =
        _millisecondsBetweenReminderAndNow


    private val _tvLeftTimeDays = MutableLiveData("00")
    val tvLeftTimeDays: LiveData<String> = _tvLeftTimeDays

    private val _tvLeftTimeHours = MutableLiveData("00")
    val tvLeftTimeHours: LiveData<String> = _tvLeftTimeHours

    private val _tvLeftTimeMinutes = MutableLiveData("00")
    val tvLeftTimeMinutes: LiveData<String> = _tvLeftTimeMinutes

    private val _tvLeftTimeSeconds = MutableLiveData("00")
    val tvLeftTimeSeconds: LiveData<String> = _tvLeftTimeSeconds

    private val _showDay = MutableLiveData<String>()
    val showDay: LiveData<String> = _showDay

    private val _showMonth = MutableLiveData<String>()
    val showMonth: LiveData<String> = _showMonth

    private val _currentReminder = MutableLiveData<Reminder>()
    private val currentReminder: LiveData<Reminder> = _currentReminder

    private val _reminderTitle = MutableLiveData<String>()
    val reminderTitle: LiveData<String> = _reminderTitle

    private val _category = MutableLiveData(Categories.OTHER.ordinal)
    val category: LiveData<Int> = _category

    val categoryIcon: Int = Categories.values()[category.value!!].drawable

    private val _calendar = MutableLiveData<Long>()
    val calendar: LiveData<Long> = _calendar

    var differenceTimeBetweenReminderAndNow: Long = 0

    init {
        parseArgument()
        showDate()
        _calendar.value = Calendar.getInstance().timeInMillis
        object : CountDownTimer(differenceTimeBetweenReminderAndNow, 1000) {
            override fun onTick(l: Long) {
                formatMilliSecondsToTime(l)
                val leftTime = leftTime.value.toString().split(" ")
                when (leftTime.size) {
                    2 -> {
                        _tvLeftTimeSeconds.value = leftTime[0]
                    }
                    4 -> {
                        _tvLeftTimeMinutes.value = leftTime[0]
                        _tvLeftTimeSeconds.value = leftTime[2]
                    }
                    6 -> {
                        _tvLeftTimeHours.value = leftTime[0]
                        _tvLeftTimeMinutes.value = leftTime[2]
                        _tvLeftTimeSeconds.value = leftTime[4]
                    }
                    8 -> {
                        _tvLeftTimeDays.value = leftTime[0]
                        _tvLeftTimeHours.value = leftTime[2]
                        _tvLeftTimeMinutes.value = leftTime[4]
                        _tvLeftTimeSeconds.value = leftTime[6]
                    }
                }
            }

            override fun onFinish() {
                isEventEnded(true)
                //tvExpired.text = getString(R.string.this_reminder_has_expired)
            }
        }.start()

    }

    private fun parseArgument() {
        if (savedStateHandle.get<Reminder>("reminder") != null) {
            _currentReminder.value = savedStateHandle.get("reminder")
        }
        savedStateHandle.get<Reminder>("reminder").let {
            if (it == null) return
            _reminderTitle.value = it.title
            val pickedDate = it.date
            val pickedTime = it.time
            setPickedDate(pickedDate)
            setPickedTime(pickedTime)
            calculateMillisecondsFromDateAndTime(pickedDate, pickedTime).also { long ->
                formatMilliSecondsToTime(long - Date().time)
            }
            _category.value = it.category
        }

        differenceTimeBetweenReminderAndNow =
            millisecondsBetweenReminderAndNow.value?.minus(Date().time) ?: 0

        Log.d("AdapterDate", "--$differenceTimeBetweenReminderAndNow")

    }


    fun setPickedDate(newDate: String) {
        _pickedDate.value = newDate
    }


    fun updateReminderTitle(s: Editable) {
        _reminderTitle.value = s.toString()
    }

    fun setPickedTime(newTime: String) {
        _pickedTime.value = newTime
        _displayedHour.value = newTime.trim().split(":")[0].trim()
        _displayedMinutes.value = newTime.trim().split(":")[1].trim()
    }

    private fun calculateMillisecondsFromDateAndTime(date: String?, time: String): Long {
        //String date_ = date;
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("tr"))
        try {
            val mDate: Date = sdf.parse(date!!)!!
            var timeInMilliseconds = mDate.time

            val split = time.split(":")
            val hour = split[0].trim()
            val min = split[1].trim()
            timeInMilliseconds += convertMinutesToMilliseconds(hour, min)
            _millisecondsBetweenReminderAndNow.value = timeInMilliseconds
            return timeInMilliseconds
        } catch (e: ParseException) {

            e.printStackTrace()
        }
        return 0
    }

    private fun convertMinutesToMilliseconds(hour: String, minutes: String): Int {
        var milliseconds = 0
        milliseconds += (minutes.toInt() * 60000)
        milliseconds += (hour.toInt() * 60000 * 60)
        return milliseconds
    }


    fun showDate() {
        val split = pickedDate.value?.split("/")
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


    fun isEventEnded(boolean: Boolean) {
        _isEventEnded.value = boolean
    }


    fun updateReminder() {
        if (!isEventEnded.value!!) {
            println("reminder" + pickedTime.value!! + "  ")
            val reminder = currentReminder.value!!.copy(
                title = reminderTitle.value!!,
                date = pickedDate.value!!,
                time = pickedTime.value!!,
                category = category.value!!
            )
            viewModelScope.launch {
                repository.updateReminder(reminder)
            }
            _statusMessage.value = "Reminder Updated"

        }
    }

    fun deleteReminder() {
        viewModelScope.launch {
            if (currentReminder.value != null) {
                repository.deleteReminder(currentReminder.value!!)
            }
        }
    }


    fun parseDateIntoDayMonthYear(): List<String> {
        return pickedDate.value!!.split("/")
    }

    fun formatMilliSecondsToTime(milliseconds: Long): String {
        var dates = ""
        val seconds = (milliseconds / 1000).toInt() % 60
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60)).toInt() % 24
        val days = (milliseconds / (1000 * 60 * 60 * 24)).toInt()
        if (days > 0) dates += "$days Days "
        if (days > 0 || hours > 0) dates += twoDigitString(hours.toLong()) + " Hrs "
        if (minutes >= 0) dates += twoDigitString(minutes.toLong()) + " Min " + twoDigitString(
            seconds.toLong()
        ) + " Secs"
        println("date$dates")
        leftTime.value = dates
        //leftTimeForAdapter = leftTime.value!!.split(" ")

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


}