package com.canbazdev.myreminders.model

import androidx.annotation.ColorInt

data class Category(
    var title: String,
    @ColorInt
    var color: Int,
    var isSelected: Boolean = false

    )