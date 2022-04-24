package com.canbazdev.myreminders.ui.add_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.adapter.CategoryAdapter
import com.canbazdev.myreminders.data.model.Category
import com.canbazdev.myreminders.databinding.FragmentAddReminderBinding
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.util.enum.Categories
import com.canbazdev.myreminders.util.helpers.BackButtonHelper
import com.canbazdev.myreminders.util.hideKeyboard
import com.canbazdev.myreminders.util.intResourceToString
import com.canbazdev.myreminders.util.toUpperCase
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddReminderFragment :
    BaseFragment<FragmentAddReminderBinding>(R.layout.fragment_add_reminder),
    CategoryAdapter.OnCategoryClickedListener,
    BackButtonHelper {

    private lateinit var categoriesAdapter: CategoryAdapter
    private lateinit var rvCategories: RecyclerView

    val viewModel: AddReminderViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        rvCategories = binding.rvCategories
        categoriesAdapter = CategoryAdapter(this)
        binding.rvCategories.adapter = categoriesAdapter

        val categories = addCategories()
        categoriesAdapter.setCategoriesListList(categories)

        val yourDatePicker =
            datePickerBuilder(
                viewModel.currentYear.value!!,
                viewModel.currentMonth.value!!,
                viewModel.currentDay.value!!
            )

        binding.btnSelectDate.setOnClickListener {
            view.hideKeyboard()
            yourDatePicker.datePicker.minDate = viewModel.currentTime.value!!.timeInMillis
            yourDatePicker.show()
        }


        binding.btnSelectTime.setOnClickListener {
            val timePicker = timePickerBuilder(
                viewModel.displayedHour.value!!.toInt(),
                viewModel.displayedMinutes.value!!.toInt(),
                TimeFormat.CLOCK_24H,
                getString(R.string.select_event_time)
            )

            timePicker.showNow(parentFragmentManager, "")

            timePicker.addOnPositiveButtonClickListener {
                val time = "${timePicker.hour.toString().trim()}:${
                    timePicker.minute.toString().trim()
                }"

                viewModel.setPickedEventTime(time)

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
                viewModel.pickedEventDate.value?.isEmpty()!! -> {
                    showLongToast(getString(R.string.reminder_date_not_be_empty))
                }
                viewModel.pickedEventTime.value?.isEmpty()!! -> {
                    showLongToast(getString(R.string.reminder_time_not_be_empty))
                }
                else -> {
                    viewModel.setReminderTitle(binding.etTitle.text.toString().trim())
                    viewModel.insertReminder()
                    showShortToast(getString(R.string.saved))
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
                viewModel.setPickedEventDate(date)
                viewModel.showDate()

            }, datePickerYear, datePickerMonth, datePickerDay
        )
    }


    override fun onItemClicked(position: Int, category: Category) {
        viewModel.setCategoryNumber(Categories.values()[position].ordinal)
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

    override fun clickBackButton() {
        findNavController().navigate(R.id.action_addReminderFragment_to_reminderFragment)
    }


}