package com.canbazdev.myreminders.ui.detail_reminder

import android.os.Bundle
import android.view.View
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
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class DetailReminderFragment :
    BaseFragment<FragmentDetailReminderBinding>(R.layout.fragment_detail_reminder) {

    private val args: DetailReminderFragmentArgs by navArgs()

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
        }


        viewModel.toastMessage.observe(viewLifecycleOwner) { text ->
            text.getContentIfNotHandled()?.let {
                showShortToast(it)
            }
        }
        binding.btnSaveReminder.setOnClickListener {
            val reminder = binding.reminder!!.copy(
                title = binding.etTitle.text.toString(),
                date = binding.btnSelectDate.text.toString(),
                time = binding.btnSelectTime.text.toString()
            )
            viewModel.updateReminder(reminder)
            showShortToast("Reminder Updated")

        }
        binding.btnDeleteReminder.setOnClickListener {
            val reminder = binding.reminder!!.copy(
                title = binding.etTitle.text.toString(),
                date = binding.btnSelectDate.text.toString(),
                time = binding.btnSelectTime.text.toString()
            )
            viewModel.deleteReminder(reminder)
            showShortToast("Reminder Deleted")
            goToRemindersFragmentFromDetailFragment()

        }

    }

    private fun goToRemindersFragmentFromDetailFragment() {
        findNavController().navigate(R.id.action_detailReminderFragment_to_reminderFragment)
    }

}