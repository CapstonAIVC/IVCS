package com.example.ivcs_android.streaming

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Switch
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial

class StreamingViewModel(val model: StreamingModel) : ViewModel() {

    val myHeight: LiveData<Int> get() = model.myHeight
    val textCount: LiveData<String> get() = model.textCount
    val textCountShow: LiveData<Boolean> get() = model.textCountShow
    val densityImageShow: LiveData<Boolean> get() = model.densityImageShow

    fun clickCountSwitch(view: View) {
        if (!model.checkSocketState()) {
            (view as SwitchMaterial).isChecked = false
            return
        }
        val checked = (view as SwitchMaterial).isChecked
        if(checked){
            model.startCounting()
        }else{
            model.stopCounting()
        }
    }

    fun clickDensitySwitch(view: View) {
        val checked = (view as SwitchMaterial).isChecked
        if(checked){
            model.startDensity()
        }else{
            model.stopDensity()
        }
    }

    companion object class StreamingFactory(val model: StreamingModel): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return StreamingViewModel(model) as T
        }
    }

}