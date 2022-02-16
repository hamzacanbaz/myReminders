package com.canbazdev.myreminders.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "text")
    val title: String,

    @ColumnInfo(name = "date")
    val date: String = ""


)