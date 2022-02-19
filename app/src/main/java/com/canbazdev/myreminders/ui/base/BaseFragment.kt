package com.canbazdev.myreminders.ui.base

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.canbazdev.myreminders.R
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.DelicateCoroutinesApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@DelicateCoroutinesApi
abstract class BaseFragment<DB : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    Fragment() {
    private var _binding: DB? = null
    val binding: DB get() = _binding!!


    open fun DB.initialize() {}


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.initialize()
        return _binding!!.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun showShortToast(displayedText: String) {
        return Toast.makeText(context, displayedText, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(displayedText: String) {
        return Toast.makeText(context, displayedText, Toast.LENGTH_LONG).show()
    }

    fun getSystemHourFormat(): Int {
        val isSystem24Hour = DateFormat.is24HourFormat(context)
        return if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
    }

    fun datePickerFormatToNormalFormat(text: String): String {

        val inputFormat = SimpleDateFormat(R.string.input_date_format.toString(), Locale.US)
        val outputFormat = SimpleDateFormat(R.string.output_date_format.toString(), Locale.US)
        var date: Date? = null
        var outputDate: String? = " "

        try {
            date = inputFormat.parse(text)
            outputDate = outputFormat.format(date!!)
            Log.e("Log ", "str $outputDate")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "$outputDate/00/00/00"

    }


}