package com.canbazdev.myreminders.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.ui.add_reminder.AddReminderFragment
import com.canbazdev.myreminders.ui.add_reminder.AddReminderFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                when (fragment) {
                    is RemindersFragment -> {
                        val layoutInflater = LayoutInflater.from(this)
                        val alertDialog: View =
                            layoutInflater.inflate(R.layout.dialog_close_app, null)
                        val builder = AlertDialog.Builder(this).create()
                        val tvCloseButton =
                            alertDialog.findViewById<TextView>(R.id.tv_dialog_yes_button)
                        val tvCancelButton =
                            alertDialog.findViewById<TextView>(R.id.tv_dialog_no_button)

                        builder.setView(alertDialog)
                        builder.show()

                        tvCloseButton.setOnClickListener {
                            builder.dismiss()
                            super.onBackPressed()
                        }
                        tvCancelButton.setOnClickListener { builder.dismiss() }

                    }
                    is AddReminderFragment -> {
                        findNavController(R.id.nav_host_fragment).navigate(
                            AddReminderFragmentDirections.actionAddReminderFragmentToReminderFragment()
                        )
                    }
                    else -> {
                        super.onBackPressed()
                    }
                }
            }
        }
    }


}