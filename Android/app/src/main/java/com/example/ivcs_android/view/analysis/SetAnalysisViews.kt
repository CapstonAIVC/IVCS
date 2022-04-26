package com.example.ivcs_android.view.analysis

import android.content.Context
import android.util.Log
import android.widget.RadioButton
import androidx.core.view.get
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas

class SetAnalysisViews(context: Context, mBinding: ActivityAnalysisBinding) {

    /*
    * AnalysisActivity의 cctv선택, 그래프 규격 선택 등을 담당
    * */

    var context = context
    var binding = mBinding

    fun setViews(){
        setRadioBts()
        setListView()
    }

    fun setRadioBts(){

        binding.rBtDay.tag = Consts.day
        binding.rBtMonth.tag = Consts.month
        binding.rBtYear.tag = Consts.year

        binding.rBtGroup.setOnCheckedChangeListener { radioGroup, checkedViewId ->
            Datas.instance.analType = binding.root.findViewById<RadioButton>(checkedViewId).tag as String
            Datas.instance.analysisTypeChange.onNext(true)
        }
    }

    fun setListView(){
        binding.analCctvList.setOnItemClickListener { _,_,i,_ ->
            Datas.instance.analName = Datas.instance.arrForListView[i]
            Datas.instance.analysisTypeChange.onNext(true)
        }
    }
}