package com.canbazdev.myreminders.util.enum

import com.canbazdev.myreminders.R

/*
*   Created by hamzacanbaz on 1.03.2022
*/
enum class Categories(val colorInt: Int, val drawable: Int) {
    WORK(R.color.green_light, R.drawable.work),
    EDUCATION(R.color.orange, R.drawable.education),
    FAMILY(R.color.red_light, R.drawable.family),
    HOME(R.color.purple_light, R.drawable.home),
    PERSONAL(R.color.pink_light, R.drawable.personal),
    FRIENDSHIP(R.color.blue_light, R.drawable.friendship),
    FUN(R.color.green, R.drawable.`fun`),
    OTHER(R.color.warning_stroke_color, R.drawable.other)
}