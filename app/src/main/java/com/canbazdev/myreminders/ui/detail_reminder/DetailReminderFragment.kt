package com.canbazdev.myreminders.ui.detail_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.databinding.FragmentDetailReminderBinding
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.util.helpers.BackButtonHelper
import com.canbazdev.myreminders.util.hideKeyboard
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailReminderFragment :
    BaseFragment<FragmentDetailReminderBinding>(R.layout.fragment_detail_reminder),
    BackButtonHelper {

    val viewModel: DetailReminderViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        binding.btnSelectTime.setOnClickListener {
            if (viewModel.isEventEnded.value == false) {

                val timePicker = timePickerBuilder(
                    viewModel.displayedHour.value!!.toInt(),
                    viewModel.displayedMinutes.value!!.toInt(),
                    CLOCK_24H,
                    getString(R.string.select_event_time)
                )
                timePicker.showNow(parentFragmentManager, "")

                timePicker.addOnPositiveButtonClickListener {
                    val time = "${timePicker.hour.toString().trim()}:${
                        timePicker.minute.toString().trim()
                    }"
                    viewModel.setPickedTime(time)

                }
            }
        }

        val eventSplitDate = viewModel.parseDateIntoDayMonthYear()
        val yourDatePicker =
            datePickerBuilder(
                eventSplitDate[2].toInt(),
                eventSplitDate[1].toInt(),
                eventSplitDate[0].toInt()
            )

        binding.btnSelectDate.setOnClickListener {
            if (binding.btnSelectDate.isEnabled) {
                view.hideKeyboard()
                yourDatePicker.datePicker.minDate = viewModel.calendar.value!!
                yourDatePicker.show()
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { text ->
            showShortToast(text)
        }


        binding.btnDeleteReminder.setOnClickListener {
            val alert = AlertView(
                getString(R.string.delete_reminder),
                getString(R.string.do_you_wanna_delete_it),
                AlertStyle.DIALOG
            )
            alert.addAction(AlertAction(getString(R.string.delete), AlertActionStyle.NEGATIVE) {
                viewModel.deleteReminder()
                showShortToast(getString(R.string.reminder_deleted))
                goToRemindersFragmentFromDetailFragment()

            })
            alert.addAction(AlertAction(getString(R.string.cancel), AlertActionStyle.DEFAULT) { })

            alert.show(activity as AppCompatActivity)

        }

        binding.ivBackButton.setOnClickListener {
            clickBackButton()
        }

    }


    private fun datePickerBuilder(
        datePickerYear: Int,
        datePickerMonth: Int,
        datePickerDay: Int
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
                viewModel.setPickedDate(date)
                viewModel.showDate()

            }, datePickerYear, datePickerMonth - 1, datePickerDay
        )
    }


    private fun goToRemindersFragmentFromDetailFragment() {
        findNavController().navigate(R.id.action_detailReminderFragment_to_reminderFragment)
    }

    override fun clickBackButton() {
        findNavController().navigate(R.id.action_detailReminderFragment_to_reminderFragment)
    }

}