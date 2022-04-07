package com.canbazdev.myreminders.ui.detail_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.databinding.FragmentDetailReminderBinding
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import com.canbazdev.myreminders.util.enum.Categories
import com.canbazdev.myreminders.util.helpers.BackButtonHelper
import com.canbazdev.myreminders.util.hideKeyboard
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
@AndroidEntryPoint
class DetailReminderFragment :
    BaseFragment<FragmentDetailReminderBinding>(R.layout.fragment_detail_reminder),
    BackButtonHelper {

    private val args: DetailReminderFragmentArgs by navArgs()

    private var pickedDate: String = ""
    private var pickedTime: String = ""
    private val mcurrentTime: Calendar = Calendar.getInstance()
    private var millisecondsBetweenReminderAndNow = 0L
    private var leftTimeCountDownTimer: CountDownTimer? = null
    var isEnded = false
    private var category = Categories.OTHER.ordinal

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: RemindersViewModel by viewModels()

        args.let {
            binding.reminder = args.reminder
            binding.viewModel = viewModel
            pickedDate = args.reminder!!.date
            pickedTime = args.reminder!!.time
            millisecondsBetweenReminderAndNow =
                calculateMillisecondsFromDateAndTime(pickedDate, pickedTime)
            category = args.reminder!!.category

            val date = Date().time
            viewModel.formatMilliSecondsToTime(millisecondsBetweenReminderAndNow - date)
        }

        val differenceTimeBetweenReminderAndNow: Long =
            millisecondsBetweenReminderAndNow - Date().time

        Log.d("AdapterDate", "--$differenceTimeBetweenReminderAndNow")

        leftTimeCountDownTimer =
            object : CountDownTimer(differenceTimeBetweenReminderAndNow, 1000) {
                override fun onTick(l: Long) {
                    viewModel.formatMilliSecondsToTime(l)
                    val leftTime = viewModel.leftTime.value.toString().split(" ")
                    println("size" + leftTime.size)
                    when (leftTime.size) {
                        2 -> {
                            binding.tvLeftTimeSeconds.text = leftTime[0]
                        }
                        4 -> {
                            binding.tvLeftTimeMinutes.text = leftTime[0]
                            binding.tvLeftTimeSeconds.text = leftTime[2]
                        }
                        6 -> {
                            binding.tvLeftTimeHours.text = leftTime[0]
                            binding.tvLeftTimeMinutes.text = leftTime[2]
                            binding.tvLeftTimeSeconds.text = leftTime[4]
                        }
                        8 -> {
                            binding.tvLeftTimeDays.text = leftTime[0]
                            binding.tvLeftTimeHours.text = leftTime[2]
                            binding.tvLeftTimeMinutes.text = leftTime[4]
                            binding.tvLeftTimeSeconds.text = leftTime[6]
                        }
                    }
                }

                override fun onFinish() {
                    isEnded = true
                    binding.tvLeftTimeDays.visibility = GONE
                    binding.tvLeftTimeHours.visibility = GONE
                    binding.tvLeftTimeMinutes.visibility = GONE
                    binding.tvLeftTimeSeconds.visibility = GONE
                    binding.tvLeftTimeDaysText.visibility = GONE
                    binding.tvLeftTimeHoursText.visibility = GONE
                    binding.tvLeftTimeMinutesText.visibility = GONE
                    binding.tvLeftTimeSecondsText.visibility = GONE
                    binding.tvExpired.text = getString(R.string.this_reminder_has_expired)
                }
            }.start()




        if (isEnded) {
            binding.btnSelectDate.isEnabled = false
            binding.btnSelectTime.isEnabled = false
            binding.etTitle.isEnabled = false
            binding.btnSaveReminder.isEnabled = false
        }
//        binding.btnSelectDate.text = pickedDate
        showDate(binding.tvDateDay, binding.tvDateYear, pickedDate)
        showTime(
            binding.tvTimeHour,
            binding.tvTimeMinute,
            pickedTime.trim().split(":")[0].trim().toInt(),
            pickedTime.trim().split(":")[1].trim().toInt()
        )

        binding.btnSelectTime.setOnClickListener {
            if (binding.tvTimeHour.isEnabled) {

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
                    pickedTime = "${timePicker.hour.toString().trim()}:${
                        timePicker.minute.toString().trim()
                    }"
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
        }

        binding.ivCategoryIcon.setImageResource(Categories.values()[category].drawable)

        val eventSplitDate = parseDateIntoDayMonthYear()
        val yourDatePicker =
            datePickerBuilder(
                binding.tvDateDay,
                binding.tvDateYear,
                eventSplitDate[2].toInt(),
                eventSplitDate[1].toInt(),
                eventSplitDate[0].toInt()
            )

        binding.btnSelectDate.setOnClickListener {
            if (binding.btnSelectDate.isEnabled) {
                view.hideKeyboard()

                yourDatePicker.datePicker.minDate = mcurrentTime.timeInMillis
                yourDatePicker.show()

            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { text ->
            text.getContentIfNotHandled()?.let {
                showShortToast(it)
            }
        }
        binding.btnSaveReminder.setOnClickListener {
            if (binding.btnSaveReminder.isEnabled) {
                val reminder = binding.reminder!!.copy(
                    title = binding.etTitle.text.toString(),
                    date = formatDate(pickedDate),
                    time = formatTime(pickedTime.trim())
                )
                viewModel.updateReminder(reminder)
                showShortToast(getString(R.string.reminder_updated))
            }
        }
        binding.btnDeleteReminder.setOnClickListener {
            val alert = AlertView(
                getString(R.string.delete_reminder),
                getString(R.string.do_you_wanna_delete_it),
                AlertStyle.DIALOG
            )
            alert.addAction(AlertAction(getString(R.string.delete), AlertActionStyle.NEGATIVE) {
                val reminder = binding.reminder!!.copy(
                    title = binding.etTitle.text.toString(),
                    date = pickedDate,
                    time = pickedTime
                )
                viewModel.deleteReminder(reminder)
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

    private fun goToRemindersFragmentFromDetailFragment() {
        findNavController().navigate(R.id.action_detailReminderFragment_to_reminderFragment)
    }

    private fun datePickerBuilder(
        tvDay: TextView,
        tvMonthAndYear: TextView,
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
                showDate(tvDay, tvMonthAndYear, date)
                pickedDate = date

            }, datePickerYear, datePickerMonth - 1, datePickerDay
        )
    }

    private fun parseDateIntoDayMonthYear(): List<String> {
        return pickedDate.split("/")
    }

    override fun onDestroyView() {
        leftTimeCountDownTimer!!.cancel()
        super.onDestroyView()
    }

    override fun clickBackButton() {
        findNavController().navigate(R.id.action_detailReminderFragment_to_reminderFragment)
    }

}