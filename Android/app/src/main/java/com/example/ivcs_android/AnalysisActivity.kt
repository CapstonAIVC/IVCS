package com.example.ivcs_android

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.view.analysis.SetAnalysisViews
import com.example.ivcs_android.viewModel.AnalysisViewModel
import com.example.ivcs_android.viewModel.AnalysisWork

class AnalysisActivity : AppCompatActivity() {

    lateinit var analysisBinding : ActivityAnalysisBinding
//    lateinit var analysisWork : AnalysisWork
//    lateinit var setAnalysisViews : SetAnalysisViews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analysisBinding = DataBindingUtil.setContentView(this, R.layout.activity_analysis)
        analysisBinding.lifecycleOwner = this
        analysisBinding.viewModel = AnalysisViewModel()


//        analysisBinding.imageAnalysis.layoutParams.height = resources.displayMetrics.heightPixels/3
//
//        analysisWork = AnalysisWork(this, analysisBinding)
//        analysisWork.bindForAnalysis()
//
//        setAnalysisViews = SetAnalysisViews(this,analysisBinding)
//        setAnalysisViews.setViews()
    }
}