package com.canbazdev.myreminders.ui.detail_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
class DetailReminderFragment :
    BaseFragment<FragmentDetailReminderBinding>(R.layout.fragment_detail_reminder) {

    private val args: DetailReminderFragmentArgs by navArgs()

    private var pickedDate: String = ""
    private var pickedTime: String = ""
    private val mcurrentTime: Calendar = Calendar.getInstance()

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
            pickedDate = args.reminder!!.date
            pickedTime = args.reminder!!.time
        }
//        binding.btnSelectDate.text = pickedDate
        showDate(binding.tvDateDay, binding.tvDateYear, pickedDate)
        showTime(
            binding.tvTimeHour,
            binding.tvTimeMinute,
            pickedTime.trim().split(":")[0].trim().toInt(),
            pickedTime.trim().split(":")[1].trim().toInt()
        )

        binding.clTimePicker.setOnClickListener {
            val formattedTime =
                viewModel.splitTimeIntoHourAndMinute(pickedTime)
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(CLOCK_24H)
                .setHour(formattedTime.first)
                .setMinute(formattedTime.second)
                .setTitleText(getString(R.string.select_event_time))
                .build()

            timePicker.showNow(parentFragmentManager, "")

            timePicker.addOnPositiveButtonClickListener {
                pickedTime = "${timePicker.hour} : ${timePicker.minute}"
//                binding.tvTimeHour.text = "${timePicker.hour}"
//                binding.tvTimeMinute.text = "${timePicker.minute}"
                showTime(
                    binding.tvTimeHour,
                    binding.tvTimeMinute,
                    timePicker.hour,
                    timePicker.minute
                )
            }
        }

        val eventSplittedDate = parseDateIntoDayMonthYear()
        val yourDatePicker =
            datePickerBuilder(
                binding.tvDateDay,
                binding.tvDateYear,
                eventSplittedDate[2].toInt(),
                eventSplittedDate[1].toInt(),
                eventSplittedDate[0].toInt()
            )

        binding.btnSelectDate.setOnClickListener {
            view.hideKeyboard()

            yourDatePicker.datePicker.minDate = mcurrentTime.timeInMillis
            yourDatePicker.show()
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { text ->
            text.getContentIfNotHandled()?.let {
                showShortToast(it)
            }
        }
        binding.btnSaveReminder.setOnClickListener {
            val reminder = binding.reminder!!.copy(
                title = binding.etTitle.text.toString(),
                date = pickedDate,
                time = pickedTime
            )
            viewModel.updateReminder(reminder)
            showShortToast("Reminder Updated")

        }
        binding.btnDeleteReminder.setOnClickListener {
            val alert = AlertView("Delete Reminder", "Do you want to delete it?", AlertStyle.DIALOG)
            alert.addAction(AlertAction("Delete", AlertActionStyle.NEGATIVE) {
                val reminder = binding.reminder!!.copy(
                    title = binding.etTitle.text.toString(),
                    date = pickedDate,
                    time = pickedTime
                )
                viewModel.deleteReminder(reminder)
                showShortToast("Reminder Deleted")
                goToRemindersFragmentFromDetailFragment()

            })
            alert.addAction(AlertAction("Cancel", AlertActionStyle.DEFAULT) { })

            alert.show(activity as AppCompatActivity)

        }

    }

    private fun goToRemindersFragmentFromDetailFragment() {
        findNavController().navigate(R.id.action_detailReminderFragment_to_reminderFragment)
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


            }, myear, mmonth - 1, mday
        )
    }

    private fun parseDateIntoDayMonthYear(): List<String> {
        return pickedDate.split("/")
    }

}