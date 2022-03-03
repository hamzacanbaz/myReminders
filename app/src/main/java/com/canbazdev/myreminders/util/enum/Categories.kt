package com.canbazdev.myreminders.util.enum

import com.canbazdev.myreminders.R

/*
*   Created by hamzacanbaz on 1.03.2022
*/
enum class Categories(val colorInt: Int) {
    WORK(R.color.green_light),
    EDUCATION(R.color.orange),
    FAMILY(R.color.red_light),
    HOME(R.color.purple_light),
    PERSONAL(R.color.pink_light),
    FRIENDSHIP(R.color.blue_light),
    OTHER(R.color.warning_stroke_color)
}