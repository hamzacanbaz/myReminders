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
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.main.MainActivity
import com.canbazdev.myreminders.util.helpers.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
@DelicateCoroutinesApi
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
        println("receive")
        super.onReceive(context, intent)
        if (intent != null) {
            if (intent.action == "click") {
                val intentOpenApp = Intent(context, MainActivity::class.java)
                intentOpenApp.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context?.startActivity(intentOpenApp)
            }
        }
    }


    @DelicateCoroutinesApi
    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.i("ReminderWidget", "Widget updateAppWidget id: $appWidgetId")
        // TODO TARIHI GUNCELLE

        val sharedPrefRepository = SharedPrefRepository(context)
        sharedPrefRepository.setWidgetId(appWidgetId)


        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val currentDate = dateFormat.format(Date())

        val reminderDao = ReminderDatabase.getDatabase(context).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val list = repository.getTodaysAllRemindersForWidget(currentDate)

        CoroutineScope(Dispatchers.Main.immediate).launch {
            println("coroutine scopeeeee")
            val closestReminder = reminderDao.getClosestReminderTodayForWidget(
                currentDate,
                getCurrentTimeWithNormalFormat()
            )
            println("rem2" + closestReminder)
            val millisecondsBetweenReminderAndNow =
                calculateMillisecondsFromDateAndTime(closestReminder.date, closestReminder.time)
            val date = Date().time
            val betweenTime = formatMilliSecondsToTime(millisecondsBetweenReminderAndNow - date)

            val views = RemoteViews(context.packageName, R.layout.left_time_for_widget)
            views.setTextViewText(R.id.tv_widget_title, closestReminder.title)
            val leftTime = betweenTime.split(" ")
            println("between time" + betweenTime)
            println("leftTime" + leftTime)
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
            println("coroutine scope exit")
        }

        val views = RemoteViews(context.packageName, R.layout.left_time_for_widget)

        views.setOnClickPendingIntent(
            R.id.ll_widget_top,
            getPendingSelfIntent(context, "click")
        )
        val intent = Intent(context, ReminderWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId)
        context.sendBroadcast(intent)

//    views.setTextViewText(R.id.appwidget_text, widgetText)
//    views.setTextViewText(R.id.appwidget_left_time, widgetTime)


        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    @DelicateCoroutinesApi
    private fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        println("clicked the pendingselfintent")
        val intent = Intent(context, ReminderWidget::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }


}




