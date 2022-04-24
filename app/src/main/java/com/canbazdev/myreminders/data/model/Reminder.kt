package com.canbazdev.myreminders.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.canbazdev.myreminders.util.enum.Categories
import java.io.Serializable

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "text")
    var title: String,

    @ColumnInfo(name = "date")
    var date: String = "",

    @ColumnInfo(name = "time")
    var time: String = "",

    @ColumnInfo(name = "category")
    var category: Int = Categories.OTHER.ordinal

) : Serializable