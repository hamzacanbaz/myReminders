//package com.canbazdev.myreminders.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.canbazdev.myreminders.data.repository.DataStoreRepository
//import com.canbazdev.myreminders.data.repository.ReminderRepository
//import com.canbazdev.myreminders.data.repository.SharedPrefRepository
//import com.canbazdev.myreminders.ui.main.RemindersViewModel
//import kotlinx.coroutines.DelicateCoroutinesApi
//
//@DelicateCoroutinesApi
//class ViewModelFactory(
//    private val repository: ReminderRepository,
//    private val dataStoreRepository: DataStoreRepository
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(RemindersViewModel::class.java)) {
//            return RemindersViewModel(repository,dataStoreRepository) as T
//        }
//        throw IllegalArgumentException("illegal ")
//    }
//
//
//}