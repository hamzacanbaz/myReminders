package com.canbazdev.myreminders.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_table")
data class Reminder(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id:Long,

    @ColumnInfo(name = "text")
    val text:String
)