package com.canbazdev.myreminders.ui.add_reminder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.adapter.CategoryAdapter
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentAddReminderBinding
import com.canbazdev.myreminders.model.Category
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.ViewModelFactory
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import com.canbazdev.myreminders.util.enum.Categories
import com.canbazdev.myreminders.util.hideKeyboard
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
class AddReminderFragment :
    BaseFragment<FragmentAddReminderBinding>(R.layout.fragment_add_reminder),
    AdapterView.OnItemSelectedListener, CategoryAdapter.OnCategoryClickedListener {

    private var pickedDate: String = ""
    private var pickedEventTime: String = ""
    private val mcurrentTime: Calendar = Calendar.getInstance()
    private val year = mcurrentTime.get(Calendar.YEAR)
    private val month = mcurrentTime.get(Calendar.MONTH)
    private val day = mcurrentTime.get(Calendar.DAY_OF_MONTH)
    private lateinit var categoriesAdapter: CategoryAdapter
    private lateinit var rvCategories: RecyclerView
    private var selectedCategory = Categories.OTHER.ordinal

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val sharedPrefRepository = SharedPrefRepository(requireContext())
        rvCategories = binding.rvCategories

        val viewModel: RemindersViewModel by viewModels {
            ViewModelFactory(repository, sharedPrefRepository)
        }

        categoriesAdapter = CategoryAdapter(this)
        binding.rvCategories.adapter = categoriesAdapter

        val categoriesList: MutableList<Category> = mutableListOf()
        categoriesList.add(Category("Work", resources.getColor(R.color.green_light)))
        categoriesList.add(Category("Education", resources.getColor(R.color.orange)))
        categoriesList.add(Category("Family", resources.getColor(R.color.red_light)))
        categoriesList.add(Category("Home", resources.getColor(R.color.purple_light)))
        categoriesList.add(Category("Personal", resources.getColor(R.color.pink_light)))
        categoriesList.add(Category("Friendship", resources.getColor(R.color.blue_light)))
        categoriesList.add(Category("Other", resources.getColor(R.color.warning_stroke_color)))
        categoriesAdapter.setCategoriesListList(categoriesList.toList())
        categoriesAdapter.notifyDataSetChanged()


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

//        ArrayAdapter.createFromResource(
//            view.context,
//            R.array.categories,
//            R.layout.item_dropdown
//        ).also { adapter ->
//            adapter.setDropDownViewResource(R.layout.item_dropdown)
//            binding.atvCategory.setAdapter(adapter)
//        }


        binding.btnSaveReminder.setOnClickListener {
//            println(binding.atvCategory.text)

            if (binding.etTitle.text.isNullOrBlank()) {
                showLongToast(getString(R.string.reminder_title_not_be_empty))
            } else if (pickedDate.isEmpty()) {
                showLongToast(getString(R.string.reminder_date_not_be_empty))
            } else if (pickedEventTime.isEmpty()) {
                showLongToast(getString(R.string.reminder_time_not_be_empty))
            } else {
                viewModel.insertReminder(
                    Reminder(
                        title = binding.etTitle.text.toString().trim(),
                        date = formatDate(pickedDate),
                        time = pickedEventTime,
                        category = selectedCategory
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

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        println(pos)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemClicked(position: Int, category: Category) {
        selectedCategory = Categories.values()[position].ordinal
    }


}