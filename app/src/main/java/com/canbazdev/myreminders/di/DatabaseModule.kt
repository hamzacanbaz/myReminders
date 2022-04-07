package com.canbazdev.myreminders.di

import android.content.Context
import androidx.room.Room
import com.canbazdev.myreminders.data.local.ReminderDatabase
import com.canbazdev.myreminders.data.local.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
*   Created by hamzacanbaz on 4.04.2022
*/
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideChannelDao(reminderDatabase: ReminderDatabase): ReminderDao {
        return reminderDatabase.reminderDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): ReminderDatabase {
        return Room.databaseBuilder(
            appContext,
            ReminderDatabase::class.java,
            "reminder_database"
        ).build()
    }
}