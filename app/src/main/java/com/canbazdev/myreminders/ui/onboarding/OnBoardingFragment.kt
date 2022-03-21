package com.canbazdev.myreminders.ui.onboarding

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.broadcastReceiver.AlarmReceiver
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentOnboardingFirstBinding
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.ViewModelFactory
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
class OnBoardingFragment :
    BaseFragment<FragmentOnboardingFirstBinding>(R.layout.fragment_onboarding_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val sharedPrefRepository = SharedPrefRepository(requireContext())

        val viewModel: RemindersViewModel by viewModels {
            ViewModelFactory(repository, sharedPrefRepository)
        }

        if (viewModel.getSavedNameFirstTime()) {
            findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
        }

        binding.tvNextButton.setOnClickListener {
            viewModel.setNameFirstTime(binding.etName.text.toString().trim())
            viewModel.setSavedNameFirstTime(true)
            // TODO remindersfragmenttaki setAlarm() metodunu kaldır buradakini aktif et
            setAlarm()
            findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun setAlarm() {
        val calendar = Calendar.getInstance()
//        if (Calendar.getInstance()[Calendar.HOUR_OF_DAY] > 9) {
//            calendar.add(Calendar.DAY_OF_YEAR, 1) // add, not set!
//        }
        calendar[Calendar.HOUR_OF_DAY] = 8
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        // TODO intent ile bildirimde gösterilecek şeyleri gönder
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, (calendar.timeInMillis),
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
//        Toast.makeText(context, "Alarm set Successfully", Toast.LENGTH_SHORT).show()
    }

}