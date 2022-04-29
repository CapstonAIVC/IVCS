package com.example.ivcs_android.view.analysis

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.example.ivcs_android.R
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import org.w3c.dom.Text

class SetAnalysisViews(context: Context, mBinding: ActivityAnalysisBinding) {

    /*
    * AnalysisActivity의 cctv선택, 그래프 규격 선택 등을 담당
    * */

    var context = context
    var binding = mBinding

    fun setViews(){
        setTextBts()
        setBts()
    }

    fun setTextBts(){
        binding.textStart.setOnClickListener {
            Datas.instance.analysisButtonSubject.onNext(Consts.setStart)
        }
        binding.textEnd.setOnClickListener {
            Datas.instance.analysisButtonSubject.onNext(Consts.setStart)
        }
        binding.textCctvName.setOnClickListener {
            Datas.instance.analysisButtonSubject.onNext(Consts.setCctv)
        }
    }

    fun setBts(){
        binding.btRequest.setOnClickListener {
            Datas.instance.analysisButtonSubject.onNext(Consts.requestAnal)
        }
    }

}