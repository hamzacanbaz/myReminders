package com.canbazdev.myreminders.util

import com.canbazdev.myreminders.util.enum.DifferentTimeTypes
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// created by Hamza Canbaz

fun findDateDifference(start_date: String?, end_date: String?, type: Int?): Long {

    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy/HH/mm/ss", Locale.US)

    var differenceBetweenDates: Long? = null
    try {
        val startDate: Date = simpleDateFormat.parse(start_date!!)!!
        val endDate: Date = simpleDateFormat.parse(end_date!!)!!
        val differenceInTime: Long = endDate.time - startDate.time

        val differenceInSeconds = ((differenceInTime / 1000) % 60)
        val differenceInMinutes = ((differenceInTime / (1000 * 60)) % 60)
        val differenceInHours = ((differenceInTime / (1000 * 60 * 60)) % 24)
        val differenceInDays = ((differenceInTime / (1000 * 60 * 60 * 24)) % 365)
        val differenceInWeeks = ((differenceInTime / (1000 * 60 * 60 * 24 * 7)) % 365)
        val differenceInYears = (differenceInTime / (1000L * 60 * 60 * 24 * 365))

        when (type) {
            DifferentTimeTypes.SECONDS.ordinal -> differenceBetweenDates = differenceInSeconds
            DifferentTimeTypes.MINUTES.ordinal -> differenceBetweenDates = differenceInMinutes
            DifferentTimeTypes.HOURS.ordinal -> differenceBetweenDates = differenceInHours
            DifferentTimeTypes.DAYS.ordinal -> differenceBetweenDates = differenceInDays
            DifferentTimeTypes.WEEKS.ordinal -> differenceBetweenDates = differenceInWeeks
            DifferentTimeTypes.YEARS.ordinal -> differenceBetweenDates = differenceInYears
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return differenceBetweenDates!!
}