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
        super.onDestroyView()
        _binding = null
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
        var date: Date? = null
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

//    fun showDate(textView: TextView, date: String) {
//        val splitted = date.split("/")
//        var newFormattedDateString = ""
//        val day = splitted[0].toInt()
//        val month = splitted[1].toInt()
//        val year = splitted[2].toInt()
//
//        newFormattedDateString += if (day < 10) "0$day/" else "$day/"
//        newFormattedDateString += if (month < 10) "0$month/" else "$month/"
//
//        newFormattedDateString += "$year"
//        textView.text = newFormattedDateString
//
//    }

    fun showDate(tvday: TextView, tvmonth: TextView, date: String) {

        val splitted = date.split("/")
        var day = splitted[0]
        var month = splitted[1].toInt()
        var monthText = ""
        val year = splitted[2]

        day = if (day.length < 2) "0$day" else day

        when (month) {
            1 -> monthText = "Jan"
            2 -> monthText = "Feb"
            3 -> monthText = "Mar"
            4 -> monthText = "Apr"
            5 -> monthText = "May"
            6 -> monthText = "Jun"
            7 -> monthText = "Jul"
            8 -> monthText = "Aug"
            9 -> monthText = "Sep"
            10 -> monthText = "Oct"
            11 -> monthText = "Nov"
            12 -> monthText = "Dec"
        }

        tvday.text = day
        tvmonth.text = "$monthText $year"

    }

    fun showTime(tvHour: TextView, tvMinute: TextView, hour: Int, minute: Int) {
        val displayedHour: String = if (hour.toString().length < 2) "0$hour" else "$hour"
        val displayedMinute: String = if (minute.toString().length < 2) "0$minute" else "$minute"

        tvHour.text = displayedHour
        tvMinute.text = displayedMinute


    }

    fun formatDate(date: String): String {
        val splitted = date.split("/")
        var newFormattedDateString = ""
        val day = splitted[0].toInt()
        val month = splitted[1].toInt()
        val year = splitted[2].toInt()

        newFormattedDateString += if (day < 10) "0$day/" else "$day/"
        newFormattedDateString += if (month < 10) "0$month/" else "$month/"

        newFormattedDateString += "$year"
        return newFormattedDateString

    }


}