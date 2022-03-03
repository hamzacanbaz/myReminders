package com.canbazdev.myreminders.ui.add_reminder

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class ReminderWidget : AppWidgetProvider() {
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
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    Log.i("ReminderWidget", "Widget updateAppWidget id: $appWidgetId")
    // TODO should be no reminders text
    var widgetText = ""
    var widgetTime = ""

    val sharedPrefRepository = SharedPrefRepository(context)
    sharedPrefRepository.setWidgetId(appWidgetId)


    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val currentDate = dateFormat.format(Date())

    val reminderDao = ReminderDatabase.getDatabase(context).reminderDao()
    val repository = ReminderRepository(reminderDao)
    val list = repository.getTodaysAllRemindersForWidget(currentDate)

    if (list.isNotEmpty()) {
        widgetText = list[0].title
        widgetTime = list[0].date
    } else {
        widgetText = ""
        widgetTime = ""

    }


    val views = RemoteViews(context.packageName, R.layout.reminder_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)
    views.setTextViewText(R.id.appwidget_left_time, widgetTime)


    appWidgetManager.updateAppWidget(appWidgetId, views)
}
