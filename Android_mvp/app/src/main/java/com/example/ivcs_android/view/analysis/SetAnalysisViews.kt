package com.example.ivcs_android.view.analysis

import android.content.Context
import android.widget.*
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import io.reactivex.rxjava3.subjects.PublishSubject

class SetAnalysisViews(context: Context, mBinding: ActivityAnalysisBinding, eventSubject : PublishSubject<String>) {

    /*
    * AnalysisActivity의 cctv선택, 그래프 규격 선택 등을 담당
    * */

    var context = context
    var binding = mBinding
    var eventSubject = eventSubject

    fun setViews(){
        setTextBts()
        setBts()
        setRadioBts()
    }

    fun setTextBts(){
        binding.textStart.setOnClickListener {
            eventSubject.onNext(Consts.setStart)
        }
        binding.textEnd.setOnClickListener {
            eventSubject.onNext(Consts.setEnd)
        }
        binding.textCctvName.setOnClickListener {
            eventSubject.onNext(Consts.setCctv)
        }
    }

    fun setBts(){
        binding.btRequest.setOnClickListener {
            eventSubject.onNext(Consts.requestAnal)
        }
    }

    fun setRadioBts(){

        binding.rBtHour.tag = Consts.hour
        binding.rBtDay.tag = Consts.day
        binding.rBtMonth.tag = Consts.month

        binding.rBtGroup.setOnCheckedChangeListener { _, checkedViewId ->
            eventSubject.onNext( binding.root.findViewById<RadioButton>(checkedViewId).tag as String )
        }

        binding.rBtHour.performClick()
    }

}