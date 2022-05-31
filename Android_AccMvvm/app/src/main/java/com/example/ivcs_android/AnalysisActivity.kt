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
        analysisBinding.rBtHour.tag = Consts.hour
        analysisBinding.rBtDay.tag = Consts.day
        analysisBinding.rBtMonth.tag = Consts.month

        analysisBinding.rBtHour.performClick()
    }

    object BindAdapter{
        // xml에 바인딩 하기 위함
        @BindingAdapter("bindImageBitmap")
        @JvmStatic
        fun loadImage(iv : ImageView, bitmap : Bitmap?) {
            // bitmap이 null이 아닐때만 실행됨
            bitmap?.let {
                iv.setImageBitmap(it)
            }
        }
    }
}