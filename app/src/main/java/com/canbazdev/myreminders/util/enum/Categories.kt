package com.canbazdev.myreminders.util.enum

import com.canbazdev.myreminders.R

/*
*   Created by hamzacanbaz on 1.03.2022
*/
enum class Categories(val colorInt: Int, val drawable: Int, val localName: Int) {
    WORK(R.color.green_light, R.drawable.work, R.string.work),
    EDUCATION(R.color.orange, R.drawable.education, R.string.education),
    FAMILY(R.color.red_light, R.drawable.family, R.string.family),
    HOME(R.color.purple_light, R.drawable.home, R.string.home),
    PERSONAL(R.color.pink_light, R.drawable.personal, R.string.personal),
    FRIENDSHIP(R.color.blue_light, R.drawable.friendship, R.string.friendship),
    FUN(R.color.green, R.drawable.`fun`, R.string.`fun`),
    OTHER(R.color.warning_stroke_color, R.drawable.other, R.string.other)
}