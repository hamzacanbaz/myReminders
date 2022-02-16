package com.canbazdev.myreminders.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canbazdev.myreminders.model.Reminder
import com.canbazdev.myreminders.repository.ReminderRepository
import com.canbazdev.myreminders.repository.SharedPrefRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class RemindersViewModel(private val repository: ReminderRepository, private val sharedPrefRepository: SharedPrefRepository): ViewModel() {

    val reminderList : LiveData<List<Reminder>> = getAllReminders()
//    private val mShowProgressBarUserInfo: MutableLiveData<Boolean> = MutableLiveData(true)
//    val showProgressBarUserInfo: LiveData<Boolean> get() = mShowProgressBarUserInfo
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    init {
        isLoading.value = true
        if (!getSavedDataFirstTime()) {
            GlobalScope.launch {
                repeat(3) {
                    val reminder = Reminder(
                        it.toLong(),
                        "Yonunu bilmeyen gemiye hicbir ruzgar yardim etmez."
                    )
                    insertReminder(reminder)
                }

            }
            setSavedDataFirstTime(true)
        }
    }

    private fun insertReminder(reminder:Reminder){
        viewModelScope.launch {
            repository.insertReminder(reminder)
        }
    }
    fun updateReminder(reminder:Reminder){
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
    }
    fun deleteReminder(reminder:Reminder){
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }
    fun deleteAllReminders(){
        viewModelScope.launch {
            repository.deleteAllReminders()
        }
    }
    private fun getAllReminders(): LiveData<List<Reminder>> {
//        mShowProgressBarUserInfo.postValue(false)
        return repository.getAllReminders()
    }

    private fun getSavedDataFirstTime(): Boolean{
        return sharedPrefRepository.getDataFirstTime()
    }

    private fun setSavedDataFirstTime(savedFirstTime: Boolean){
        sharedPrefRepository.setDataFirstTime(savedFirstTime)
    }
}