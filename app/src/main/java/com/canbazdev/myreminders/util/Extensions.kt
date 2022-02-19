package com.canbazdev.myreminders.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


// created by Hamza Canbaz

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
}