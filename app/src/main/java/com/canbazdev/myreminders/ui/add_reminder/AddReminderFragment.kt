package com.canbazdev.myreminders.ui.add_reminder

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentAddReminderBinding
import com.canbazdev.myreminders.model.Reminder
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
class AddReminderFragment :
    BaseFragment<FragmentAddReminderBinding>(R.layout.fragment_add_reminder) {

    private var pickedDate: String = ""
    private var pickedEventTime: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val sharedPrefRepository = SharedPrefRepository(requireContext())

        val viewModel: RemindersViewModel by viewModels {
            ViewModelFactory(repository, sharedPrefRepository)
        }

        binding.btnSelectDate.setOnClickListener {
            view.hideKeyboard()
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.select_date))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.showNow(parentFragmentManager, "")

            datePicker.addOnPositiveButtonClickListener {
//                val currentDate = getCurrentDateWithNormalFormat()
//                val reminderDate = datePickerFormatToNormalFormat(datePicker.headerText.toString())

//                findDateDifference(currentDate, reminderDate)
                binding.btnSelectDate.text = datePicker.headerText.toString()
                pickedDate = datePicker.headerText.toString()

            }
        }
        binding.btnSelectTime.setOnClickListener {
            val systemHourFormat = getSystemHourFormat()
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(systemHourFormat)
                .setHour(12)
                .setMinute(10)
                .setTitleText(getString(R.string.select_event_time))
                .build()


            timePicker.showNow(parentFragmentManager, "")

            timePicker.addOnPositiveButtonClickListener {
                binding.btnSelectTime.text = "${timePicker.hour} : ${timePicker.minute}"
                pickedEventTime = "${timePicker.hour} : ${timePicker.minute}"

            }

        }

        binding.etTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) view.hideKeyboard()
        }

        binding.btnSaveReminder.setOnClickListener {
            pickedDate


            if (binding.etTitle.text.isNullOrBlank()) {
                showLongToast(getString(R.string.reminder_title_not_be_empty))
            } else if (pickedDate.isEmpty()) {
                // TODO selected date should be after today
                showLongToast(getString(R.string.reminder_date_not_be_empty))
            } else if (pickedEventTime.isEmpty()) {
                showLongToast(getString(R.string.reminder_time_not_be_empty))
            } else {
                viewModel.insertReminder(
                    Reminder(
                        title = binding.etTitle.text.toString().trim(),
                        date = pickedDate,
                        time = pickedEventTime
                    )
                )
                showShortToast(getString(R.string.saved))
                clearInputAreas()
                goToRemindersFromAddReminderFragment()
            }
        }

    }

    private fun goToRemindersFromAddReminderFragment() {
        findNavController().navigate(R.id.action_addReminderFragment_to_reminderFragment)
    }

    private fun clearInputAreas() {
        binding.btnSelectTime.text = ""
        binding.btnSelectDate.text = ""
        binding.etTitle.setText("")
        binding.etTitle.clearFocus()
        pickedDate = ""
        pickedDate = ""
    }


}