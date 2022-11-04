package com.example.ivcs_android.analysis

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ivcs_android.analysis.pickDialog.CctvPickDialogFragment
import com.example.ivcs_android.analysis.pickDialog.DatePickDialogFragment

class AnalysisViewModel(val model: AnalysisModel, val fragmentManager: FragmentManager) : ViewModel(){

    val analImage: LiveData<Bitmap> get() = model.analImage
    val startText: LiveData<String> get() = model.startText
    val endText: LiveData<String> get() = model.endText
    val cctvText: LiveData<String> get() = model.cctvText

    fun radioClicked(view: View) {
        model.analType = view.tag as String
    }

    fun requestBtClicked() {
        if (model.checkRequestAnalysis()) model.analysisDataRequest.onNext(true)
    }


    fun clickStartText() {
        var dial = DatePickDialogFragment(model)
        dial.arguments = Bundle().also { it.putBoolean("isStart", true) }
        dial.show(fragmentManager, "DatePickDialogFragment")
    }

    fun clickEndText() {
        var dial = DatePickDialogFragment(model)
        dial.arguments = Bundle().also { it.putBoolean("isStart", false) }
        dial.show(fragmentManager, "DatePickDialogFragment")
    }

    fun cctvNameClicked() {
        var dial = CctvPickDialogFragment(model)
        dial.show(fragmentManager, "CctvPickDialogFragment")
    }

    companion object class AnalysisViewModelFactory(val analysisModel: AnalysisModel, val fragmentManager: FragmentManager): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return AnalysisViewModel(analysisModel, fragmentManager) as T
        }

    }
}