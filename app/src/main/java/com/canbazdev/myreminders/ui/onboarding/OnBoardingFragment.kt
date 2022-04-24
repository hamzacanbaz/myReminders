package com.canbazdev.myreminders.ui.onboarding

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.broadcastReceiver.AlarmReceiver
import com.canbazdev.myreminders.databinding.FragmentOnboardingFirstBinding
import com.canbazdev.myreminders.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class OnBoardingFragment :
    BaseFragment<FragmentOnboardingFirstBinding>(R.layout.fragment_onboarding_first) {

    val viewModel: OnBoardingViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewmodel = viewModel

        viewModel.getSavedNameFirstTime.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved) {
                goToReminderFragment()
            } else {
                binding.cl.visibility = View.VISIBLE
            }
        }

        viewModel.goToMainFragment.observe(viewLifecycleOwner) {
            if (it) {
                setAlarm()
                goToReminderFragment()
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun goToReminderFragment() {
        findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
    }

    private fun setAlarm() {

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 0)
        }


        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(context, 0, it, FLAG_IMMUTABLE)
            } else {
                PendingIntent.getBroadcast(context, 0, it, 0)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, (calendar.timeInMillis),
            1000 * 60 * 2, intent
        )
        Toast.makeText(context, "Alarm set Successfully", Toast.LENGTH_SHORT).show()
    }

}