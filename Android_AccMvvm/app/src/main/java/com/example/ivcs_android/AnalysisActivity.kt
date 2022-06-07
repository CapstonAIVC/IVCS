package com.example.ivcs_android

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.viewModel.analysis.AnalysisViewModel

class AnalysisActivity : AppCompatActivity() {

    lateinit var analysisBinding : ActivityAnalysisBinding
    lateinit var analysisViewModel : AnalysisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analysisBinding = DataBindingUtil.setContentView(this, R.layout.activity_analysis)
        analysisBinding.lifecycleOwner = this
        analysisViewModel = AnalysisViewModel(application, supportFragmentManager)
        analysisBinding.viewModel = analysisViewModel
        setRadioBts()
        analysisViewModel.dataAnal.analImageHeight = resources.displayMetrics.heightPixels/5*2
    }

    fun setRadioBts(){
        analysisBinding.rBtMinute.tag = Consts.minute
        analysisBinding.rBtHour.tag = Consts.hour
        analysisBinding.rBtDay.tag = Consts.day

        analysisBinding.rBtHour.performClick()
    }
}