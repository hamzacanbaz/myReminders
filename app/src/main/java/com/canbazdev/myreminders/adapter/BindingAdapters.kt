package com.canbazdev.myreminders.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.canbazdev.myreminders.R
import com.canbazdev.myreminders.data.model.Reminder

/*
*   Created by hamzacanbaz on 24.04.2022
*/

@BindingAdapter("isVisible")
fun setIsVisible(view: View, isEventEnded: Boolean) {
    view.visibility = if (isEventEnded) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("isEnabled")
fun setIsEnabled(view: View, isEventEnded: Boolean) {
    view.isEnabled = !isEventEnded
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, drawable: Int?) {
    if (drawable != null) {
        Glide.with(view.context).load(drawable).into(view)
    }
}

@BindingAdapter("android:progressVisibility")
fun setVisibility(view: View, isLoading: Boolean) {
    if (!isLoading) {
        view.visibility = View.GONE
    }
}

@BindingAdapter("android:setReminderCount")
fun setText(view: TextView, count: Int) {
    when {
        count > 0 -> {
            view.text = view.context.getString(R.string.you_have_x_reminders_today, count)
        }
        count == 1 -> {
            view.text = view.context.getString(R.string.you_have_1_reminder_today)
        }
        else -> {
            view.text = view.context.getString(R.string.you_have_no_reminder_today)
        }
    }
}

@BindingAdapter("android:setUsernameText")
fun setUsernameText(view: TextView, name: String) {
    view.text = view.context.getString(R.string.hello_x, name)
}

@BindingAdapter("android:manageNoDataState")
fun manageNoDataState(textView: TextView, visibility: Boolean) {
    textView.visibility = if (visibility) View.VISIBLE else View.GONE
}

@BindingAdapter("android:manageRvState")
fun manageRvState(rv: RecyclerView, visibility: Boolean) {
    rv.visibility = if (visibility) View.VISIBLE else View.GONE
}

@BindingAdapter("android:setAdapter")
fun setAdapter(recyclerView: RecyclerView, adapter: RemindersAdapter?) {
    adapter.let { recyclerView.adapter = it }
}

@BindingAdapter("android:submitList")
fun submitList(recyclerView: RecyclerView, list: List<Reminder>?) {
    val adapter = recyclerView.adapter as RemindersAdapter?

    adapter?.setRemindersList(list ?: listOf())
}

@BindingAdapter("android:setItemDecoration")
fun submitList(recyclerView: RecyclerView, decoration: ReminderDecoration) {
    recyclerView.addItemDecoration(decoration)
}




