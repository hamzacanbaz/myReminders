package com.canbazdev.myreminders.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.canbazdev.myreminders.R


// created by Hamza Canbaz

class ReminderDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val spacing = view.resources.getDimensionPixelSize(R.dimen.margin_20dp)
        val isFirstItem = parent.getChildAdapterPosition(view) == 0
        with(outRect) {
            if (isFirstItem) top = spacing

            bottom = spacing
            right = spacing
            left = spacing

        }
    }

}