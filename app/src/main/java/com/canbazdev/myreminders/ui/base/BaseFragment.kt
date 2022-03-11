package com.canbazdev.myreminders.ui.base

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.util.enum.Months
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@DelicateCoroutinesApi
abstract class BaseFragment<DB : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    Fragment() {
    private var _binding: DB? = null
    val binding: DB get() = _binding!!

    open fun DB.initialize() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.initialize()

        return _binding!!.root
    }

    override fun onDestroyView() {
        _binding = null
        println("ondestroy from base")
        super.onDestroyView()
    }

    fun showShortToast(displayedText: String) {
        return Toast.makeText(context, displayedText, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(displayedText: String) {
        return Toast.makeText(context, displayedText, Toast.LENGTH_LONG).show()
    }

    fun getSystemHourFormat(): Int {
        val isSystem24Hour = DateFormat.is24HourFormat(context)
        return if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
    }

    fun datePickerFormatToNormalFormat(text: String): String {

        val inputFormat = SimpleDateFormat(R.string.input_date_format.toString(), Locale.US)
        val outputFormat = SimpleDateFormat(R.string.output_date_format.toString(), Locale.US)
        val date: Date?
        var outputDate: String? = " "

        try {
            date = inputFormat.parse(text)
            outputDate = outputFormat.format(date!!)
            Log.e("Log ", "str $outputDate")
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return "$outputDate/00/00/00"
    }

    fun showDate(tvDay: TextView, tvMonth: TextView, date: String) {

        val split = date.split("/")
        var day = split[0]
        val month = split[1].toInt()
        val year = split[2]

        day = if (day.length < 2) "0$day" else day
        val monthText: String = Months.values()[month - 1].toString()

        tvDay.text = day
        tvMonth.text =
            String.format(resources.getString(R.string.text_view_month_and_year), monthText, year)

    }

    fun showTime(tvHour: TextView, tvMinute: TextView, hour: Int, minute: Int) {
        val displayedHour: String = if (hour.toString().length < 2) "0$hour" else "$hour"
        val displayedMinute: String = if (minute.toString().length < 2) "0$minute" else "$minute"

        tvHour.text = displayedHour
        tvMinute.text = displayedMinute


    }

    fun formatDate(date: String): String {
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

    fun formatTime(time: String): String {
        val split = time.split(":")
        var hours = split[0]
        var minutes = split[1]
        if (hours.toInt() < 10) hours = "0$hours"
        if (minutes.toInt() < 10) minutes = "0$minutes"
        return "$hours:$minutes"
    }

    open fun convertMinutesToMilliseconds(hour: String, minutes: String): Int {
        var milliseconds = 0
        milliseconds += (minutes.toInt() * 60000)
        milliseconds += (hour.toInt() * 60000 * 60)
        return milliseconds
    }

    open fun calculateMillisecondsFromDateAndTime(date: String?, time: String): Long {
        //String date_ = date;
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        try {
            val mDate: Date = sdf.parse(date!!)!!
            var timeInMilliseconds = mDate.time

            val split = time.split(":")
            val hour = split[0].trim()
            val min = split[1].trim()
            timeInMilliseconds += convertMinutesToMilliseconds(hour, min)

            return timeInMilliseconds
        } catch (e: ParseException) {

            e.printStackTrace()
        }
        return 0
    }


}