package com.example.ivcs_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.view.analysis.SetAnalysisViews
import com.example.ivcs_android.Presenter.Anal.AnalysisViewModel

class AnalysisActivity : AppCompatActivity() {

    lateinit var analysisBinding : ActivityAnalysisBinding
    lateinit var analysisViewModel : AnalysisViewModel
    lateinit var setAnalysisViews : SetAnalysisViews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analysisBinding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(analysisBinding.root)

        analysisBinding.imageAnalysis.layoutParams.height = resources.displayMetrics.heightPixels/3

//        analysisBinding.textAnalysis.text = "평균 \n 14~15시 : 8.3543295249549\n 15~16시 : 8.307645287563986"

        analysisViewModel = AnalysisViewModel(this, analysisBinding)
        analysisViewModel.analViewModelSetting()

        setAnalysisViews = SetAnalysisViews(this,analysisBinding, analysisViewModel.getBtSubject())
        setAnalysisViews.setViews()
    }
}