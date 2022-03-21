package com.canbazdev.myreminders.util.helpers

import android.os.Build
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/*
*   Created by hamzacanbaz on 22.03.2022
*/
interface Time {
    fun twoDigitString(number: Long): String? {
        if (number == 0L) {
            return "00"
        }
        return if (number / 10 == 0L) {
            "0$number"
        } else number.toString()
    }

    fun convertMinutesToMilliseconds(hour: String, minutes: String): Int {
        var milliseconds = 0
        milliseconds += (minutes.toInt() * 60000)
        milliseconds += (hour.toInt() * 60000 * 60)
        return milliseconds
    }

    fun calculateMillisecondsFromDateAndTime(date: String?, time: String): Long {
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

    fun getCurrentTimeWithNormalFormat(): String {
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
        return dates
    }


}