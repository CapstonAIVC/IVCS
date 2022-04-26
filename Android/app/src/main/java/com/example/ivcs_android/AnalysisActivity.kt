package com.example.ivcs_android

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.view.analysis.SetAnalysisViews
import com.example.ivcs_android.viewModel.AnalysisBind
import im.dacer.androidcharts.LineView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

class AnalysisActivity : AppCompatActivity() {

    lateinit var analysisBinding : ActivityAnalysisBinding
    lateinit var analysisBind : AnalysisBind
    lateinit var setAnalysisViews : SetAnalysisViews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analysisBinding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(analysisBinding.root)

//        analysisBinding.scrollHoriz.layoutParams.height = resources.displayMetrics.heightPixels/2
        analysisBinding.imageAnalysis.layoutParams.height = resources.displayMetrics.heightPixels/3

        analysisBinding.textAnalysis.text = "평균 \n 14~15시 : 8.3543295249549\n 15~16시 : 8.307645287563986"


//        // lineView 속성 세팅
//        analysisBinding.lineView.layoutParams.height = resources.displayMetrics.heightPixels/2
//        analysisBinding.lineView.setDrawDotLine(false)
//        analysisBinding.lineView.setShowPopup(LineView.SHOW_POPUPS_All)
//
        analysisBind = AnalysisBind(this, analysisBinding)
        analysisBind.bindForAnalysis()

        setAnalysisViews = SetAnalysisViews(this,analysisBinding)
        setAnalysisViews.setViews()
//
//        // 처음 세팅
//        Datas.instance.analName = Datas.instance.arrForListView[0]
//        analysisBinding.rBtDay.performClick()
    }
}