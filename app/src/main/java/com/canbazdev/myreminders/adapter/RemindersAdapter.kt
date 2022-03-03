package com.canbazdev.myreminders.adapter

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.databinding.ItemReminderBinding
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.util.enum.Categories

class RemindersAdapter(
    private val listener: OnItemClickedListener
) :
    RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    var remindersList = ArrayList<Reminder>()

    fun setRemindersList(list: List<Reminder>) {
        remindersList.clear()
        remindersList.addAll(list)
        notifyDataSetChanged()

    }

    inner class ReminderViewHolder(private val binding: ItemReminderBinding) :
        RemindersAdapter.BaseViewHolder<Reminder>(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: Reminder) {
            binding.tvReminderTitle.text = item.title
            binding.tvRemiderDate.text = item.date
            binding.vLeftView.setBackgroundResource(R.drawable.background_left_item_reminder_view)
            // TODO deprecated method
            val drawable: Drawable = binding.vLeftView.background

            drawable.setColorFilter(
                itemView.resources.getColor(Categories.values()[item.category].colorInt),
                PorterDuff.Mode.SRC_ATOP
            );
            binding.vLeftView.background = drawable

        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClicked(position, remindersList[position])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemReminderBinding.inflate(inflater, parent, false)
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

    interface OnItemClickedListener {
        fun onItemClicked(position: Int, reminder: Reminder)
    }


}

