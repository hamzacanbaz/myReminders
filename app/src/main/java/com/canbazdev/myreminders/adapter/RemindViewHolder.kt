package com.canbazdev.myreminders.adapter

import com.canbazdev.myreminders.databinding.ItemReminderBinding
import com.canbazdev.myreminders.model.Reminder

class RemindViewHolder(private val binding: ItemReminderBinding) :
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