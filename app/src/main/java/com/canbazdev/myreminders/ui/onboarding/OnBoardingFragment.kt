package com.canbazdev.myreminders.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentOnboardingFirstBinding
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.ViewModelFactory
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.ui.main.RemindersViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class OnBoardingFragment :
    BaseFragment<FragmentOnboardingFirstBinding>(R.layout.fragment_onboarding_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val reminderDao = ReminderDatabase.getDatabase(requireContext()).reminderDao()
        val repository = ReminderRepository(reminderDao)
        val sharedPrefRepository = SharedPrefRepository(requireContext())

        val viewModel: RemindersViewModel by viewModels {
            ViewModelFactory(repository, sharedPrefRepository)
        }

        if (viewModel.getSavedNameFirstTime()) {
            findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
        }

        binding.tvNextButton.setOnClickListener {
            viewModel.setNameFirstTime(binding.etName.text.toString().trim())
            viewModel.setSavedNameFirstTime(true)

            findNavController().navigate(R.id.action_firstFragment_to_reminderFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }

}