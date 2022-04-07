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
import com.canbazdev.myreminders.databinding.FragmentOnboardingFirstBinding
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
@AndroidEntryPoint
class OnBoardingFragment :
    BaseFragment<FragmentOnboardingFirstBinding>(R.layout.fragment_onboarding_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewModel: RemindersViewModel by viewModels()


//        lifecycleScope.launch {
//            dataStoreRepository.getSavedNameFirstTime.collect {
//                if (it){
//                    findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
//                }
//            }
//        }

        // Çalışmıyordu ama şimdi nasıl çalışıyor anlamadım
        viewModel.getSavedNameFirstTime.observe(
            viewLifecycleOwner
        ) { isSaved ->
            if (isSaved) {
                findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
            }
        }


        // Test amacli yazdim
//        lifecycleScope.launch {
//            dataStoreRepository.getSavedNameFirstTime.collectLatest {
//                binding.etName.setText(it.toString())
//            }
//        }
//
//        lifecycleScope.launch {
//            dataStoreRepository.getSavedNameFirstTime.collect {
//                println(it)
//            }
//        }


        binding.tvNextButton.setOnClickListener {
            // TODO burada iki farklı metod olarak çağırınca doğru çalışmıyor neden
//                viewModel.setNameFirstTime(binding.etName.text.toString().trim())

            viewModel.setNameFirstTime(binding.etName.text.toString().trim())
            viewModel.setSavedNameFirstTime(true)

            //VEYA

//                dataStoreRepository.setSavedName(binding.etName.text.toString().trim())
//                dataStoreRepository.setSavedNameFirstTime(true)

//            lifecycleScope.launch {
//                viewModel.setSavedNameFirstTime(true)
//            }


//            viewModel.setSavedNameFirstTime(true)
            // TODO remindersfragmenttaki setAlarm() metodunu kaldır buradakini aktif et

            setAlarm()
            try {
                findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
            } catch (l: IllegalArgumentException) {
                println(l.localizedMessage)
            }

//            viewModel.setSavedNameFirstTime(true)
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