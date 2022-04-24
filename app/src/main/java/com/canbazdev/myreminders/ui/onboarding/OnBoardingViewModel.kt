package com.canbazdev.myreminders.ui.onboarding

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.canbazdev.myreminders.data.repository.DataStoreRepository
import com.canbazdev.myreminders.util.enum.Categories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/*
*   Created by hamzacanbaz on 28.04.2022
*/
@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {


    val date = Date().time

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _goToMainFragment = MutableLiveData(false)
    val goToMainFragment: LiveData<Boolean> = _goToMainFragment


    private val _category = MutableLiveData(Categories.OTHER.ordinal)
    val category: LiveData<Int> = _category

    private val _getSavedNameFirstTime = MutableLiveData<Boolean>()
    val getSavedNameFirstTime: LiveData<Boolean> = _getSavedNameFirstTime


    init {
        getSavedNameFirstTime()

    }

    fun clickNextButton() {
        println("clicked")
        setNameFirstTime(name.value!!.toString().trim())
        setSavedNameFirstTime(true)
        _goToMainFragment.postValue(true)
    }

    fun updateName(s: Editable) {
        _name.value = s.toString()
    }

    private fun getSavedNameFirstTime() {
        viewModelScope.launch {
            dataStoreRepository.getSavedNameFirstTime.collect {
                println("saved $it")
                _getSavedNameFirstTime.value = it
            }
            while (true) {
                delay(30000L)
                dataStoreRepository.setSavedNameFirstTime(true)
            }
        }

    }

    private fun setSavedNameFirstTime(savedFirstTime: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setSavedNameFirstTime(savedFirstTime)
        }
    }

    private fun setNameFirstTime(nameText: String) {
        viewModelScope.launch {
            dataStoreRepository.setSavedName(nameText)
        }
    }


}