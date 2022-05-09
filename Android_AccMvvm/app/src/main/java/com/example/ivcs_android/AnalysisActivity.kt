package com.example.ivcs_android

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.viewModel.AnalysisViewModel

class AnalysisActivity : AppCompatActivity() {

    lateinit var analysisBinding : ActivityAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analysisBinding = DataBindingUtil.setContentView(this, R.layout.activity_analysis)
        analysisBinding.lifecycleOwner = this
        analysisBinding.viewModel = AnalysisViewModel(application)

        setRadioBts()
        setBtForDialog()

        Datas.instance.analImageHeight = analysisBinding.imageAnalysis.layoutParams.height
    }

    fun setBtForDialog(){
        analysisBinding.textStart.setOnClickListener {
            var dial = DatePickDialogFragment()
            dial.arguments = Bundle().also { it.putBoolean("isStart", true) }
            dial.show(supportFragmentManager, "DatePickDialogFragment")
        }
        analysisBinding.textEnd.setOnClickListener {
            var dial = DatePickDialogFragment()
            dial.arguments = Bundle().also { it.putBoolean("isStart", false) }
            dial.show(supportFragmentManager, "DatePickDialogFragment")
        }
    }

    fun setRadioBts(){
        analysisBinding.rBtHour.tag = Consts.hour
        analysisBinding.rBtDay.tag = Consts.day
        analysisBinding.rBtMonth.tag = Consts.month

        analysisBinding.rBtHour.performClick()
    }

    object bindAdapter{
        // xml에 바인딩 하기 위함
        @BindingAdapter("bind:imageBitmap")
        @JvmStatic
        fun loadImage(iv : ImageView, bitmap : Bitmap?) {
            // bitmap이 null이 아닐때만 실행됨
            bitmap?.let {
                iv.setImageBitmap(it)
            }
        }
    }
}