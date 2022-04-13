package com.example.ivcs_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import im.dacer.androidcharts.LineView

class AnalysisActivity : AppCompatActivity() {

    lateinit var analisysBinding : ActivityAnalysisBinding
    lateinit var lineView : LineView
    var bottomArr = ArrayList<String>()
    var dataArr = ArrayList<ArrayList<Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analisysBinding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(analisysBinding.root)

        //test//
        dataArr.add( arrayListOf(1,4,7,5,3,6,8,4,30,7,40) )
        for(i in 0..10){
            bottomArr.add(i.toString())
        }
        setLineView()
    }

    fun setLineView(){
        lineView = analisysBinding.lineView
        lineView.layoutParams.height = resources.displayMetrics.heightPixels/2
        lineView.setDrawDotLine(false)
        lineView.setShowPopup(LineView.SHOW_POPUPS_All)
        lineView.setBottomTextList(bottomArr)
        lineView.setDataList(dataArr); //or lineView.setFloatDataList(floatDataLists)
    }
}