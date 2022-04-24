package com.canbazdev.myreminders.ui.main

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.adapter.ReminderDecoration
import com.canbazdev.myreminders.adapter.RemindersAdapter
import com.canbazdev.myreminders.databinding.FragmentRemindersBinding
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.util.Constants.CHANNEL_ID
import com.canbazdev.myreminders.util.Constants.NOTIFICATION_ID
import com.canbazdev.myreminders.util.enum.Categories
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class RemindersFragment : BaseFragment<FragmentRemindersBinding>(R.layout.fragment_reminders) {

    private lateinit var remindersAdapter: RemindersAdapter
    val viewModel: RemindersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        createNotificationChannel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        remindersAdapter = RemindersAdapter(viewModel)
        binding.viewModel = viewModel
        binding.itemDecoration = ReminderDecoration()
        binding.adapter = remindersAdapter
//        setAlarm()

        viewModel.goToDetail.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.setGoToDetail()
                viewModel.currentReminder.observe(viewLifecycleOwner) {
                    val action =
                        RemindersFragmentDirections.actionReminderFragmentToDetailReminderFragment(
                            viewModel.currentReminder.value
                        )
                    findNavController().navigate(action)
                }


            }
        }



        binding.tvHelloName.setOnClickListener {
            val layoutInflater = LayoutInflater.from(view.context)
            val alertDialog: View = layoutInflater.inflate(R.layout.dialog_change_name, null)
            val builder = AlertDialog.Builder(view.context).create()
            val etAlertDialogName = alertDialog.findViewById<TextInputEditText>(R.id.et_name)
            etAlertDialogName.setText(viewModel.savedName.value)

            builder.setView(alertDialog)


            alertDialog.findViewById<Button>(R.id.btn_save_name).setOnClickListener {
                val name = etAlertDialogName.text.toString()
                viewModel.setNameFirstTime(name)
                showShortToast(getString(R.string.name_saved))
                builder.dismiss()
                viewModel.getNameFirstTime()

            }
            builder.show()


        }

        binding.fabGoToAddReminder.setOnClickListener {
            goToAddReminderFragment()
        }

        observeReminderList(viewModel)

        setUpSwipeToDeleteReminders(viewModel)

    }

    private fun observeReminderList(
        viewModel: RemindersViewModel
    ) {
        viewModel.reminderList.observe(viewLifecycleOwner) { reminderList ->
            sendNotification(
                "MyReminders",
                "${reminderList.size} adet reminder var",
                R.drawable.friendship
            )
            println("reminder list is observing")
            if (reminderList != null && reminderList.isNotEmpty()) {

                val appWidgetManager = AppWidgetManager.getInstance(context)
                var flag = 0
                var index = 0
                var reminder = reminderList[index]
                while (flag == 0 && index <= reminderList.size - 1) {

                    reminder = reminderList[index]
                    val millisecondsBetweenReminderAndNow =
                        calculateMillisecondsFromDateAndTime(reminder.date, reminder.time)
                    val date = Date().time
                    viewModel.formatMilliSecondsToTime(millisecondsBetweenReminderAndNow - date)
                    if (!viewModel.leftTime.value.isNullOrEmpty()) {
                        flag = 1
                    } else {
                        index++
                    }
                }


                val reminderTitleWithCapitalise =
                    reminder.title.lowercase(Locale.getDefault()).replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase(Locale.getDefault())
                        else char.toString()
                    }

                val views = RemoteViews(
                    context?.packageName,
                    R.layout.left_time_for_widget
                ).also {
                    val leftTime = viewModel.leftTime.value.toString().split(" ")
//                    println("left time$leftTime")
                    it.setTextViewText(R.id.tv_leftTime_minutes, "00")
                    it.setTextViewText(R.id.tv_leftTime_hours, "00")
                    it.setTextViewText(R.id.tv_leftTime_days, "00")

                    when (leftTime.size) {
                        4 -> {
                            it.setTextViewText(R.id.tv_leftTime_minutes, leftTime[0])
                        }
                        6 -> {
                            it.setTextViewText(R.id.tv_leftTime_hours, leftTime[0])
                            it.setTextViewText(R.id.tv_leftTime_minutes, leftTime[2])
                        }
                        8 -> {
                            if (leftTime[0].toInt() < 10) {
                                it.setTextViewText(R.id.tv_leftTime_days, "0${leftTime[0]}")
                            } else {
                                it.setTextViewText(R.id.tv_leftTime_days, leftTime[0])
                            }
                            it.setTextViewText(R.id.tv_leftTime_hours, leftTime[2])
                            it.setTextViewText(R.id.tv_leftTime_minutes, leftTime[4])
                        }
                    }

                    it.setTextViewText(R.id.tv_widget_title, reminderTitleWithCapitalise)
                    it.setImageViewResource(
                        R.id.iv_widget_category,
                        Categories.values()[reminder.category].drawable
                    )
//                        it.setTextViewText(R.id.appwidget_left_time, reminder.time)
                }
                val widgetId = viewModel.widgetId.value!!
                println("widget id $widgetId")
                appWidgetManager.updateAppWidget(widgetId, views)


            } else {
//                binding.rvReminders.visibility = View.GONE
//                binding.noDataFound.visibility = View.VISIBLE
//                progressBar.visibility = View.GONE
            }
        }

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
                val position = viewHolder.layoutPosition
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
                setDeleteIcon(c, viewHolder, dX, isCurrentlyActive)
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
        viewHolder: RecyclerView.ViewHolder,
        dx: Float,
        isCurrently: Boolean,
    ) {
        val mClearPaint = Paint()
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val mBg = ColorDrawable()
        val bgColor = Color.parseColor("#b80f0a")
        val deleteDrawable: Drawable? =
            ContextCompat.getDrawable(requireContext(), R.drawable.trash)
        val intrinsicsWidth = deleteDrawable!!.intrinsicWidth
        val intrinsicsHeight = deleteDrawable.intrinsicHeight

        val itemView: View = viewHolder.itemView
        val itemHeight = itemView.height
        val cancelled = dx == 0f && !isCurrently

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


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Title"
            val descriptionText = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(
                    requireContext(),
                    NotificationManager::class.java
                ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification(title: String, text: String, icon: Int) {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

//    override fun onItemClicked(position: Int, reminder: Reminder) {
//        val action =
//            RemindersFragmentDirections.actionReminderFragmentToDetailReminderFragment(reminder)
//        findNavController().navigate(action)
//    }

    private fun goToAddReminderFragment() {
        findNavController().navigate(R.id.action_reminderFragment_to_addReminderFragment)
    }

}