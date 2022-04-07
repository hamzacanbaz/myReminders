package com.canbazdev.myreminders.ui.add_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.adapter.CategoryAdapter
import com.canbazdev.myreminders.databinding.FragmentAddReminderBinding
import com.canbazdev.myreminders.model.Category
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import com.canbazdev.myreminders.util.enum.Categories
import com.canbazdev.myreminders.util.helpers.BackButtonHelper
import com.canbazdev.myreminders.util.hideKeyboard
import com.canbazdev.myreminders.util.intResourceToString
import com.canbazdev.myreminders.util.toUpperCase
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
@AndroidEntryPoint
class AddReminderFragment :
    BaseFragment<FragmentAddReminderBinding>(R.layout.fragment_add_reminder),
    AdapterView.OnItemSelectedListener, CategoryAdapter.OnCategoryClickedListener,
    BackButtonHelper {

    private val mcurrentTime: Calendar = Calendar.getInstance()
    private val year = mcurrentTime.get(Calendar.YEAR)
    private val month = mcurrentTime.get(Calendar.MONTH)
    private val day = mcurrentTime.get(Calendar.DAY_OF_MONTH)

    private var pickedEventTime: String = ""

    private var pickedDate: String = ""
    private lateinit var categoriesAdapter: CategoryAdapter
    private lateinit var rvCategories: RecyclerView
    private var selectedCategory = Categories.OTHER.ordinal

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickedEventTime =
            "${mcurrentTime.get(Calendar.HOUR_OF_DAY)}:${mcurrentTime.get(Calendar.MINUTE)}"
        rvCategories = binding.rvCategories

        pickedDate =
            "${mcurrentTime.get(Calendar.DAY_OF_MONTH)}/${mcurrentTime.get(Calendar.MONTH) + 1}/${
                mcurrentTime.get(Calendar.YEAR)
            }"

        val viewModel: RemindersViewModel by viewModels()

        showInitialDateAndTime()

        categoriesAdapter = CategoryAdapter(this)
        binding.rvCategories.adapter = categoriesAdapter
        val categories = addCategories()
        categoriesAdapter.setCategoriesListList(categories)
        categoriesAdapter.notifyDataSetChanged()

        val yourDatePicker =
            datePickerBuilder(binding.tvDateDay, binding.tvDateYear, year, month, day)

        binding.btnSelectDate.setOnClickListener {
            view.hideKeyboard()
            yourDatePicker.datePicker.minDate = mcurrentTime.timeInMillis
            yourDatePicker.show()
        }
        binding.btnSelectTime.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(CLOCK_24H)
                .setHour(mcurrentTime.get(Calendar.HOUR_OF_DAY))
                .setMinute(mcurrentTime.get(Calendar.MINUTE))
                .setTitleText(getString(R.string.select_event_time))
                .build()


            timePicker.showNow(parentFragmentManager, "")

            timePicker.addOnPositiveButtonClickListener {
                pickedEventTime =
                    "${timePicker.hour.toString().trim()}:${timePicker.minute.toString().trim()}"
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

            when {
                binding.etTitle.text.isNullOrBlank() -> {
                    showLongToast(getString(R.string.reminder_title_not_be_empty))
                }
                pickedDate.isEmpty() -> {
                    showLongToast(getString(R.string.reminder_date_not_be_empty))
                }
                pickedEventTime.isEmpty() -> {
                    showLongToast(getString(R.string.reminder_time_not_be_empty))
                }
                else -> {
                    viewModel.insertReminder(
                        Reminder(
                            title = binding.etTitle.text.toString().trim(),
                            date = formatDate(pickedDate),
                            time = formatTime(pickedEventTime.trim()),
                            category = selectedCategory
                        )
                    )
                    showShortToast(getString(R.string.saved))
                    clearInputAreas()
                    goToRemindersFromAddReminderFragment()
                }
            }
        }
        binding.ivBackButton.setOnClickListener {
            clickBackButton()
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

            }, datePickerYear, datePickerMonth, datePickerDay
        )
    }

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        println(pos)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemClicked(position: Int, category: Category) {
        selectedCategory = Categories.values()[position].ordinal
    }

    private fun addCategories(): List<Category> {
        val categoriesList: MutableList<Category> = mutableListOf()
        categoriesList.add(
            Category(
                Categories.WORK.localName.intResourceToString(requireContext()).toUpperCase(),
                ContextCompat.getColor(requireContext(), Categories.WORK.colorInt)
            )
        )
        categoriesList.add(
            Category(
                Categories.EDUCATION.localName.intResourceToString(requireContext()).toUpperCase(),
                ContextCompat.getColor(requireContext(), Categories.EDUCATION.colorInt)
            )
        )
        categoriesList.add(
            Category(
                Categories.FAMILY.localName.intResourceToString(requireContext()).toUpperCase(),
                ContextCompat.getColor(requireContext(), Categories.FAMILY.colorInt)
            )
        )
        categoriesList.add(
            Category(
                Categories.HOME.localName.intResourceToString(requireContext()).toUpperCase(),
                ContextCompat.getColor(requireContext(), Categories.HOME.colorInt)
            )
        )
        categoriesList.add(
            Category(
                Categories.PERSONAL.localName.intResourceToString(requireContext()).toUpperCase(),
                ContextCompat.getColor(requireContext(), Categories.PERSONAL.colorInt)
            )
        )
        categoriesList.add(
            Category(
                Categories.FRIENDSHIP.localName.intResourceToString(requireContext()).toUpperCase(),
                ContextCompat.getColor(requireContext(), Categories.FRIENDSHIP.colorInt)
            )
        )

        categoriesList.add(
            Category(
                Categories.FUN.localName.intResourceToString(requireContext()).lowercase()
                    .replaceFirstChar {
                        it.uppercase()
                    },
                ContextCompat.getColor(requireContext(), Categories.FUN.colorInt)
            )
        )

        categoriesList.add(
            Category(
                Categories.OTHER.localName.intResourceToString(requireContext()).lowercase()
                    .replaceFirstChar {
                        it.uppercase()
                    },
                ContextCompat.getColor(requireContext(), Categories.OTHER.colorInt)
            )
        )
        return categoriesList.toList()
    }

    private fun showInitialDateAndTime() {
        val date = String.format(
            "%d/%d/%d",
            day,
            month + 1,
            year
        )
        showDate(binding.tvDateDay, binding.tvDateYear, date)
        showTime(
            binding.tvTimeHour,
            binding.tvTimeMinute,
            mcurrentTime.get(Calendar.HOUR_OF_DAY),
            mcurrentTime.get(Calendar.MINUTE)
        )

    }

    override fun clickBackButton() {
        findNavController().navigate(R.id.action_addReminderFragment_to_reminderFragment)
    }


}