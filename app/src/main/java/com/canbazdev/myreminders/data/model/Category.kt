package com.canbazdev.myreminders.data.model

import androidx.annotation.ColorInt

data class Category(
    var title: String,
    @ColorInt
    var color: Int,
    var isSelected: Boolean = false

    )