package com.canbazdev.myreminders.ui.main

import android.app.*
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.adapter.ReminderDecoration
import com.canbazdev.myreminders.adapter.RemindersAdapter
import com.canbazdev.myreminders.broadcastReceiver.AlarmReceiver
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.databinding.FragmentRemindersBinding
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import com.canbazdev.myreminders.ui.ViewModelFactory
import com.canbazdev.myreminders.ui.base.BaseFragment
import com.canbazdev.myreminders.util.enum.Categories
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*


@DelicateCoroutinesApi
open class RemindersFragment : BaseFragment<FragmentRemindersBinding>(R.layout.fragment_reminders),
    RemindersAdapter.OnItemClickedListener {

    private lateinit var rvReminders: RecyclerView
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var progressBar: ProgressBar
    private val channelId = "foxandroid"
    private val notificationId = 101
    private var todayReminderNumber: Int = 0

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

        binding.tvHelloName.setOnClickListener {
            setAlarm()
            val layoutInflater = LayoutInflater.from(view.context)
            val alertDialog: View = layoutInflater.inflate(R.layout.dialog_change_name, null)
            val builder = AlertDialog.Builder(view.context).create()
            val etAlertDialogName = alertDialog.findViewById<TextInputEditText>(R.id.et_name)
            etAlertDialogName.setText(viewModel.savedName.value)

            builder.setView(alertDialog)


            alertDialog.findViewById<Button>(R.id.btn_save_name).setOnClickListener {
                val name = etAlertDialogName.text.toString()
                sharedPrefRepository.setNameFirstText(name)
                showShortToast(getString(R.string.name_saved))
                builder.dismiss()
                viewModel.getNameFirstTime()

            }
            builder.show()


        }

        viewModel.reminderList.observe(viewLifecycleOwner) { reminderList ->
//            sendNotification("MyReminders", "${it.size} adet reminder var", R.drawable.friendship)
            if (reminderList != null && reminderList.isNotEmpty()) {
                viewModel.isLoading.value = false
                remindersAdapter.setRemindersList(reminderList)

                println("observe all reminders")
                println(reminderList)

                val appWidgetManager = AppWidgetManager.getInstance(context)
                var flag = 0
                var index = 0
                var reminder = reminderList[index]
                println("flag $flag index $index")
                while (flag == 0 && index <= reminderList.size - 1) {
                    println("while flag $flag index $index")

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
                    println("left time$leftTime")
                    // TODO kalan zamani ekle
                    // TODO reminder yoksa bugün boşsunuz yazdır
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

                val widgetId = sharedPrefRepository.getWidgetId()
                appWidgetManager.updateAppWidget(widgetId, views)

            } else {
                binding.noDataFound.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }

        viewModel.todaysReminderList.observe(viewLifecycleOwner) {

            todayReminderNumber = it.size
            if (todayReminderNumber != 0) {
                binding.tvTodayReminderNumbers.text =
                    String.format(
                        resources.getString(R.string.today_you_have_x_reminders),
                        todayReminderNumber
                    )

            } else {
                binding.tvTodayReminderNumbers.text =
                    resources.getString(R.string.you_have_no_reminders)
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


//                val appWidgetManager = AppWidgetManager.getInstance(context)
//                val remoteViews =
//                    RemoteViews(requireContext().packageName, R.layout.reminder_widget).also {
//                        it.setTextViewText(R.id.appwidget_text, reminderTitleWithCapitalise)
//                        it.setTextViewText(R.id.appwidget_left_time, reminder.time)
//                    }
//                val widgetId = sharedPrefRepository.getWidgetId()
//                appWidgetManager.partiallyUpdateAppWidget(widgetId, remoteViews)


            } else {
                setTodayReminderVisibility()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (!it) progressBar.visibility = View.GONE
        }

        setUpSwipeToDeleteReminders(viewModel)

        viewModel.savedName.observe(viewLifecycleOwner) {
            binding.tvHelloName.text = String.format(resources.getString(R.string.hello_x), it)
        }

    }


    private fun setTodayReminderVisibility() {
        binding.tvTodayReminderTitle.text = resources.getString(R.string.you_are_free)
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
            val channel = NotificationChannel(channelId, name, importance).apply {
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
        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, builder.build())
        }
    }

    private fun setAlarm() {
        val calendar = Calendar.getInstance()
        if (Calendar.getInstance()[Calendar.HOUR_OF_DAY] > 9) {
            calendar.add(Calendar.DAY_OF_YEAR, 1) // add, not set!
        }
        calendar[Calendar.HOUR_OF_DAY] = 9
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        // TODO intent ile bildirimde gösterilecek şeyleri gönder
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, (calendar.timeInMillis),
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
        Toast.makeText(context, "Alarm set Successfully", Toast.LENGTH_SHORT).show()
    }


}