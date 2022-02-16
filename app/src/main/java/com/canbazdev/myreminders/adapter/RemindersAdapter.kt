package com.canbazdev.myreminders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.databinding.RowItemReminderBinding
import com.canbazdev.myreminders.model.Reminder

class RemindersAdapter(

) :
    RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    private var remindersList = ArrayList<Reminder>()

    fun setRemindersList(list: List<Reminder>) {
        remindersList.clear()
        remindersList.addAll(list)
        println(list.size)
        notifyDataSetChanged()

    }

    inner class ReminderViewHolder(private val binding: RowItemReminderBinding) :
        RemindersAdapter.BaseViewHolder<Reminder>(binding.root) {

        //        init {
//            itemView.setOnClickListener(this)
//        }
        override fun bind(item: Reminder) {
            binding.tvReminder.text = item.title
        }

//        override fun onClick(p0: View?) {
//            val position = adapterPosition
//            if (position != RecyclerView.NO_POSITION) {
//                listener.onItemClicked(position, NAME)
//            }
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RowItemReminderBinding.inflate(inflater, parent, false)
        return ReminderViewHolder(binding)
    }

//    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
//    }

    override fun getItemCount(): Int {
        return remindersList.size
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Reminder)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(remindersList[position])

    }

}

