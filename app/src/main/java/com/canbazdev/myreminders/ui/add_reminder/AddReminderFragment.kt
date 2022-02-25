package com.canbazdev.myreminders.ui.add_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
class AddReminderFragment :
    BaseFragment<FragmentAddReminderBinding>(R.layout.fragment_add_reminder) {

    private var pickedDate: String = ""
    private var pickedEventTime: String = ""
    private val mcurrentTime: Calendar = Calendar.getInstance()
    private val year = mcurrentTime.get(Calendar.YEAR)
    private val month = mcurrentTime.get(Calendar.MONTH)
    private val day = mcurrentTime.get(Calendar.DAY_OF_MONTH)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val sharedPrefRepository = SharedPrefRepository(requireContext())

        val viewModel: RemindersViewModel by viewModels {
            ViewModelFactory(repository, sharedPrefRepository)
        }

        val yourDatePicker =
            datePickerBuilder(binding.tvDateDay, binding.tvDateYear, year, month, day)


        binding.clDatePicker.setOnClickListener {
            view.hideKeyboard()
            yourDatePicker.datePicker.minDate = mcurrentTime.timeInMillis
            yourDatePicker.show()
        }
        binding.clTimePicker.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(CLOCK_24H)
                .setHour(12)
                .setMinute(10)
                .setTitleText(getString(R.string.select_event_time))
                .build()


            timePicker.showNow(parentFragmentManager, "")

            timePicker.addOnPositiveButtonClickListener {
                pickedEventTime = "${timePicker.hour}:${timePicker.minute}"
//                binding.btnSelectTime.text = pickedEventTime
                showTime(
                    binding.tvTimeHour,
                    binding.tvTimeMinute,
                    timePicker.hour,
                    timePicker.minute
                )

            }

        }

        binding.etTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) view.hideKeyboard()
        }

        binding.btnSaveReminder.setOnClickListener {


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
                        date = formatDate(pickedDate),
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
        binding.tvTimeHour.text = ""
        binding.tvTimeMinute.text = ""
        binding.tvDateDay.text = ""
        binding.tvDateDay.text = ""
        binding.etTitle.setText("")
        binding.etTitle.clearFocus()
        pickedDate = ""
    }

    private fun datePickerBuilder(
        tvDay: TextView,
        tvMonthAndYear: TextView,
        myear: Int,
        mmonth: Int,
        mday: Int
    ): DatePickerDialog {
        return DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val date = String.format(
                    "%d/%d/%d",
                    dayOfMonth,
                    month + 1,
                    year
                )
                showDate(tvDay, tvMonthAndYear, date)
                pickedDate = date

            }, myear, mmonth, mday
        )
    }


}