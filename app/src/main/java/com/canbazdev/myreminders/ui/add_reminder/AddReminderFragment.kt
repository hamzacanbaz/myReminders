package com.canbazdev.myreminders.ui.add_reminder

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentAddReminderBinding
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.ViewModelFactory
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.DelicateCoroutinesApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@DelicateCoroutinesApi
class AddReminderFragment :
    BaseFragment<FragmentAddReminderBinding>(R.layout.fragment_add_reminder) {

    private lateinit var pickedDate: String

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
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.showNow(parentFragmentManager, "")
            datePicker.addOnPositiveButtonClickListener {
//                val currentDate = getCurrentDateWithNormalFormat()
//                val reminderDate = datePickerFormatToNormalFormat(datePicker.headerText.toString())
//                val df: DateFormat = SimpleDateFormat("dd/MM/yyyy/HH/mm/ss", Locale.US)
//                val x = df.parse(reminderDate)
//                val y = df.parse(currentDate)
//                println("$x $y")
                binding.btnSelectDate.text = datePicker.headerText.toString()
                pickedDate = datePicker.headerText.toString()

            }
        }

        binding.etTitle.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) view.hideKeyboard()
        }

        binding.btnSaveReminder.setOnClickListener {
            if (binding.etTitle.text != null && pickedDate.isNotEmpty()) {
                viewModel.insertReminder(
                    Reminder(
                        title = binding.etTitle.text.toString(),
                        date = pickedDate
                    )
                )
            }
        }


    }

    private fun View.hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
    }

    private fun datePickerFormatToNormalFormat(text: String): String {
        val inputPattern = "MMM dd, yyyy"
        val outputPattern = "dd/MM/yyyy"
        val inputFormat = SimpleDateFormat(inputPattern, Locale.US)
        val outputFormat = SimpleDateFormat(outputPattern, Locale.US)

        var date: Date? = null
        var str: String? = " "

        try {
            date = inputFormat.parse(text)
            str = outputFormat.format(date!!)
            Log.e("Log ", "str $str")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "$str/00/00/00"

    }

    private fun getCurrentDateWithNormalFormat(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy/HH/mm/ss", Locale.US);
        return dateFormat.format(Date())

    }


    private fun getWeeksDifference(fromDate: Date?, toDate: Date?): Int {
        return if (fromDate == null || toDate == null) 0
        else ((toDate.time - fromDate.time) / (1000 * 60 * 60 * 24 * 7)).toInt()
    }

    private fun getDaysDifference(fromDate: Date?, toDate: Date?): Int {
        return if (fromDate == null || toDate == null) 0
        else ((toDate.time - fromDate.time) / (1000 * 60 * 60 * 24)).toInt()
    }

    private fun getHoursDifference(fromDate: Date?, toDate: Date?): Int {
        return if (fromDate == null || toDate == null) 0
        else ((toDate.time - fromDate.time) / (1000 * 60 * 60)).toInt() % 24
    }

}