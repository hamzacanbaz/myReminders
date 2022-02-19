package com.canbazdev.myreminders.ui.main

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.adapter.ReminderDecoration
import com.canbazdev.myreminders.adapter.RemindersAdapter
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentRemindersBinding
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.ViewModelFactory
import com.canbazdev.myreminders.ui.base.BaseFragment
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class RemindersFragment : BaseFragment<FragmentRemindersBinding>(R.layout.fragment_reminders),
    RemindersAdapter.OnItemClickedListener {

    private lateinit var rvReminders: RecyclerView;
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var progressBar: ProgressBar

    private var todayReminderNumber: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = binding.pbLoadingData

        val reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val sharedPrefRepository = SharedPrefRepository(requireContext())

        val viewModel: RemindersViewModel by viewModels {
            ViewModelFactory(repository, sharedPrefRepository)
        }


        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvReminders = binding.rvReminders
        rvReminders.layoutManager = linearLayoutManager
        rvReminders.addItemDecoration(ReminderDecoration())

        remindersAdapter = RemindersAdapter(this)
        binding.rvReminders.adapter = remindersAdapter

        binding.fabGoToAddReminder.setOnClickListener {
            findNavController().navigate(R.id.action_reminderFragment_to_addReminderFragment)
        }

        viewModel.reminderList.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                viewModel.isLoading.value = false
                remindersAdapter.setRemindersList(it)
            } else {
                binding.noDataFound.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }

        viewModel.todaysReminderList.observe(viewLifecycleOwner) {

            todayReminderNumber = it.size
// TODO String placeholder olustur
            if (todayReminderNumber != 0) {
                binding.tvTodayReminderNumbers.text =
                    "Today, you have $todayReminderNumber reminders."
            } else {
                binding.tvTodayReminderNumbers.text = "Today, you have no reminders."
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) progressBar.visibility = View.GONE
        }
    }

    override fun onItemClicked(position: Int, reminder: Reminder) {
        val action =
            RemindersFragmentDirections.actionReminderFragmentToDetailReminderFragment(reminder)
        findNavController().navigate(action)
    }

}