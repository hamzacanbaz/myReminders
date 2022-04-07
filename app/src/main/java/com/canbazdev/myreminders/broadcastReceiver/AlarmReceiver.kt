package com.canbazdev.myreminders.broadcastReceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.ui.main.MainActivity
import kotlinx.coroutines.DelicateCoroutinesApi

/*
*   Created by hamzacanbaz on 10.03.2022
*/


@DelicateCoroutinesApi
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val i = Intent(context, MainActivity::class.java)
        val notificationTitle = "Todays Reminders"
        var notificationText = ""
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "foxandroid")
            .setSmallIcon(R.drawable.add_png)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(123, builder.build())
    }
}