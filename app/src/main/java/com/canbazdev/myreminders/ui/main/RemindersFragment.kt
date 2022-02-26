package com.canbazdev.myreminders.ui.main

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

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

        viewModel.closestReminderToday.observe(viewLifecycleOwner) { reminder ->
            if (reminder != null) {
                val reminderTitleWithCapitalise =
                    reminder.title.lowercase(Locale.getDefault()).replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                        else it.toString()
                    }
                binding.tvTodayReminderTitle.text = reminderTitleWithCapitalise
                binding.tvTodayReminderTitle.text.toString()
                binding.tvTodayReminderTime.text = reminder.time
            } else {
                setTodayReminderVisibility()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) progressBar.visibility = View.GONE
        }

        setUpSwipeToDeleteReminders(viewModel)

        viewModel.savedName.observe(viewLifecycleOwner) {
            binding.tvHelloName.text = "Hello $it"
        }

    }

    private fun setTodayReminderVisibility() {
        binding.tvTodayReminderTitle.text = "You are free"
        binding.tvTodayReminderTime.text = ""

    }

    override fun onItemClicked(position: Int, reminder: Reminder) {
        val action =
            RemindersFragmentDirections.actionReminderFragmentToDetailReminderFragment(reminder)
        findNavController().navigate(action)
    }

    private fun setUpSwipeToDeleteReminders(viewModel: RemindersViewModel) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val reminder = remindersAdapter.remindersList[position]
                viewModel.deleteReminder(reminder)

                Snackbar.make(viewHolder.itemView, "Deleted reminder", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.insertReminder(reminder)
                    }
                    show()
                }
            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 1f
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvReminders)
        }


    }

    fun setDeleteIcon(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dx: Float,
        dy: Float,
        actionState: Int,
        isCurrently: Boolean,
    ) {
        val mClearPaint: Paint = Paint()
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val mBg: ColorDrawable = ColorDrawable()
        val bgColor = Color.parseColor("#b80f0a")
        val deleteDrawable: Drawable? =
            ContextCompat.getDrawable(requireContext(), R.drawable.trash)
        val intrinsicsWidth = deleteDrawable!!.intrinsicWidth
        val intrinsicsHeight = deleteDrawable.intrinsicHeight

        val itemView: View = viewHolder.itemView
        val itemHeight = itemView.height
        var cancelled = dx == 0f && !isCurrently

        if (cancelled) {
            c.drawRect(
                itemView.right + dx,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                mClearPaint
            )
            return
        }
        mBg.color = bgColor
        mBg.setBounds(itemView.right + dx.toInt(), itemView.top, itemView.right, itemView.bottom)
        mBg.draw(c)

        val deleteIconTop = itemView.top + (itemHeight - intrinsicsHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicsHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicsWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicsHeight

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable.draw(c)
    }


}