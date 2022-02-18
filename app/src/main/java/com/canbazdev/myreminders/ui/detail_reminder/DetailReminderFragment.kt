package com.canbazdev.myreminders.ui.detail_reminder

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.databinding.FragmentDetailReminderBinding
import com.canbazdev.myreminders.ui.base.BaseFragment
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class DetailReminderFragment :
    BaseFragment<FragmentDetailReminderBinding>(R.layout.fragment_detail_reminder) {

    private val args: DetailReminderFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.let {
            binding.reminder = args.reminder
        }

    }


}