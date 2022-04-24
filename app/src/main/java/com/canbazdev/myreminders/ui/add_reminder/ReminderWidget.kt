package com.canbazdev.myreminders.ui.add_reminder

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.widget.RemoteViews
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.data.model.Reminder
import com.canbazdev.myreminders.data.repository.ReminderRepository
import com.canbazdev.myreminders.ui.main.MainActivity
import com.canbazdev.myreminders.util.helpers.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * Implementation of App Widget functionality.
 */

class ReminderWidget : AppWidgetProvider(), Time {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.i("ReminderWidget", "Widget OnUpdate")

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Log.i("ReminderWidget", "Widget onEnabled")
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        Log.i("ReminderWidget", "Widget onDisabled")
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent != null) {
            if (intent.action == "click") {
                val intentOpenApp = Intent(context, MainActivity::class.java)
                intentOpenApp.addFlags(FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intentOpenApp)
            }
        }
    }


    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.i("ReminderWidget", "Widget updateAppWidget id: $appWidgetId")
        // TODO TARIHI GUNCELLE

        // TODO DataStoreRepository ekle

//        val dataStoreRepository = DataStoreRepository(context)

//        CoroutineScope(Dispatchers.Main.immediate).launch {
//            dataStoreRepository.setWidgetId(appWidgetId)
//
//        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val currentDate = dateFormat.format(Date())

        val reminderDao = ReminderDatabase.getDatabase(context).reminderDao()
        val repository = ReminderRepository(reminderDao)

        CoroutineScope(Dispatchers.Main.immediate).launch {
            val allReminders = reminderDao.getAllRemindersForWidget(currentDate)
            var flag = 0
            var index = 0
            var betweenTime2 = ""
            val date2 = Date().time
            while (flag == 0 && index < allReminders.size) {
                val milli =
                    calculateMillisecondsFromDateAndTime(
                        allReminders[index].date,
                        allReminders[index].time
                    )
                betweenTime2 = formatMilliSecondsToTime(milli - date2)
                if (betweenTime2.isNotEmpty() && betweenTime2 != "") {
                    flag = 1
                }
                index++
            }
            if (betweenTime2.isNotBlank() && betweenTime2.isNotEmpty()) {
                Log.e("ReminderWidget", "Not blank or empty")
            }


            val closestReminder: Reminder = reminderDao.getClosestReminderTodayForWidget(
                currentDate,
                getCurrentTimeWithNormalFormat()
            )
            if (closestReminder != null) {
                println("nul degil")
                val millisecondsBetweenReminderAndNow =
                    calculateMillisecondsFromDateAndTime(closestReminder.date, closestReminder.time)
                val date = Date().time
                val betweenTime = formatMilliSecondsToTime(millisecondsBetweenReminderAndNow - date)

                val views = RemoteViews(context.packageName, R.layout.left_time_for_widget)
                views.setTextViewText(R.id.tv_widget_title, closestReminder.title)
                val leftTime = betweenTime.split(" ")
                when (leftTime.size) {
                    4 -> {
                        views.setTextViewText(R.id.tv_leftTime_minutes, leftTime[0])
                    }
                    6 -> {
                        views.setTextViewText(R.id.tv_leftTime_hours, leftTime[0])
                        views.setTextViewText(R.id.tv_leftTime_minutes, leftTime[2])
                    }
                    8 -> {
                        if (leftTime[0].toInt() < 10) {
                            views.setTextViewText(R.id.tv_leftTime_days, "0${leftTime[0]}")
                        } else {
                            views.setTextViewText(R.id.tv_leftTime_days, leftTime[0])
                        }
                        views.setTextViewText(R.id.tv_leftTime_hours, leftTime[2])
                        views.setTextViewText(R.id.tv_leftTime_minutes, leftTime[4])
                    }
                }

//        views.setTextViewText(R.id.tv_leftTime_days, closestReminder.time)
                appWidgetManager.updateAppWidget(appWidgetId, views)

            }
        }

        val views = RemoteViews(context.packageName, R.layout.left_time_for_widget)

        views.setOnClickPendingIntent(
            R.id.ll_widget_top,
            getPendingSelfIntent(context, "click")
        )
        val intent = Intent(context, ReminderWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val array = intArrayOf(appWidgetId)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, array)
        context.sendBroadcast(intent)

//    views.setTextViewText(R.id.appwidget_text, widgetText)
//    views.setTextViewText(R.id.appwidget_left_time, widgetTime)


        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        val intent = Intent(context, ReminderWidget::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }


}




