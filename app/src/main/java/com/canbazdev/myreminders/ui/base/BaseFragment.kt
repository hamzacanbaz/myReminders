package com.canbazdev.myreminders.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import kotlinx.coroutines.DelicateCoroutinesApi

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

}