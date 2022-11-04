package com.example.ivcs_android.analysis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.ivcs_android.R
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import io.reactivex.rxjava3.subjects.PublishSubject

class AnalysisActivity : AppCompatActivity() {

    companion object{
        val subjectForToast: PublishSubject<String> = PublishSubject.create()
    }

    lateinit var analysisBinding : ActivityAnalysisBinding
    lateinit var analysisViewModel : AnalysisViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fac = AnalysisViewModel.AnalysisViewModelFactory(AnalysisModel(), supportFragmentManager)
        analysisViewModel = ViewModelProvider(this, fac).get(AnalysisViewModel::class.java)
        analysisBinding = DataBindingUtil.setContentView<ActivityAnalysisBinding?>(this, R.layout.activity_analysis).apply {
            lifecycleOwner = this@AnalysisActivity
            viewModel = analysisViewModel
        }
        setRadioBts()
        analysisViewModel.model.analImageHeight = resources.displayMetrics.heightPixels/5*2

        subjectForToast.subscribe{ Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
    }

    fun setRadioBts(){
        analysisBinding.rBtHour.tag = Consts.hour
        analysisBinding.rBtDay.tag = Consts.day
        analysisBinding.rBtMonth.tag = Consts.month

        analysisBinding.rBtHour.performClick()
    }
}