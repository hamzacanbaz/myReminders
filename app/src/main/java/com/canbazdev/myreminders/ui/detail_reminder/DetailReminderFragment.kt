package com.canbazdev.myreminders.ui.detail_reminder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentDetailReminderBinding
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.ViewModelFactory
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import com.canbazdev.myreminders.util.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class DetailReminderFragment :
    BaseFragment<FragmentDetailReminderBinding>(R.layout.fragment_detail_reminder) {

    private val args: DetailReminderFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val sharedPrefRepository = SharedPrefRepository(requireContext())

        val viewModel: RemindersViewModel by viewModels {
            ViewModelFactory(repository, sharedPrefRepository)
        }

        args.let {
            binding.reminder = args.reminder
            binding.viewModel = viewModel
        }
        binding.btnSelectTime.setOnClickListener {
            val formattedTime =
                viewModel.splitTimeIntoHourAndMinute(binding.btnSelectTime.text.toString())
            val systemHourFormat = getSystemHourFormat()
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(systemHourFormat)
                .setHour(formattedTime.first)
                .setMinute(formattedTime.second)
                .setTitleText(getString(R.string.select_event_time))
                .build()


            timePicker.showNow(parentFragmentManager, "")

            timePicker.addOnPositiveButtonClickListener {
                binding.btnSelectTime.text = "${timePicker.hour} : ${timePicker.minute}"
            }

        }

        binding.btnSelectDate.setOnClickListener {


            view.hideKeyboard()
            val reminderFormattedTime =
                viewModel.getReminderDateFromFormatter(
                    binding.btnSelectDate.text.toString(),
                    getString(R.string.input_date_format)
                )

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_date))
                    .setSelection(reminderFormattedTime)
                    .build()

            datePicker.showNow(parentFragmentManager, "")

            datePicker.addOnPositiveButtonClickListener {

//                findDateDifference(currentDate, reminderDate)
                binding.btnSelectDate.text = datePicker.headerText.toString()

            }
        }


        viewModel.toastMessage.observe(viewLifecycleOwner) { text ->
            text.getContentIfNotHandled()?.let {
                showShortToast(it)
            }
        }
        binding.btnSaveReminder.setOnClickListener {
            val reminder = binding.reminder!!.copy(
                title = binding.etTitle.text.toString(),
                date = binding.btnSelectDate.text.toString(),
                time = binding.btnSelectTime.text.toString()
            )
            viewModel.updateReminder(reminder)
            showShortToast("Reminder Updated")

        }
        binding.btnDeleteReminder.setOnClickListener {
            val reminder = binding.reminder!!.copy(
                title = binding.etTitle.text.toString(),
                date = binding.btnSelectDate.text.toString(),
                time = binding.btnSelectTime.text.toString()
            )
            viewModel.deleteReminder(reminder)
            showShortToast("Reminder Deleted")
            goToRemindersFragmentFromDetailFragment()

        }

    }

    private fun goToRemindersFragmentFromDetailFragment() {
        findNavController().navigate(R.id.action_detailReminderFragment_to_reminderFragment)
    }

}