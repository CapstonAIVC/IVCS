package com.example.ivcs_android.viewModel

import android.R
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import im.dacer.androidcharts.LineView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import org.json.JSONObject

class AnalysisBind(context: Context, mBinding: ActivityAnalysisBinding) {

    var context = context
    var analysisBinding = mBinding

    fun bindForAnalysis(){
        setAdapter()
        bindForChangeAnalData()
    }

    fun bindForChangeAnalData(){
        // 정보 요청
        Datas.instance.analysisTypeChange
            .subscribe(
                {
                    var str = "{\"cctvname\":\"${Datas.instance.analName}\",\"type\":\"${Datas.instance.analType}\"}"
                    Msocket.instance.mSocket.emit("req_analysis", str )
                },
                {Log.e("analysisTypeChangErr",it.message.toString())}
            )
        // 들어온 정보 처리
        Datas.instance.analysisDataChange
            .subscribe(
                {
                    var jsonObj = JSONObject(it)
                    var objArr = jsonObj.getJSONArray("data")
                    var data = arrayListOf<ArrayList<Int>>(ArrayList())
                    for( i in 0 until objArr.length()){
                        data[0].add( objArr[i] as Int)
                    }
                    var bottom : ArrayList<String> = when( jsonObj.get("type") ){
                        Consts.day -> { Consts.bottomDay }
                        Consts.month -> { Consts.bottomMonth }
                        else -> { Consts.bottomYear }
                    }
                    setLineView(data,bottom)
                },
                { Log.e("analysisDataChangeErr",it.message.toString()) }
            )
    }

    fun setLineView(data : ArrayList<ArrayList<Int>>, bottom : ArrayList<String>){
        Observable.just(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                analysisBinding.lineView.setBottomTextList(bottom)
                analysisBinding.lineView.setDataList(data) //or lineView.setFloatDataList(floatDataLists)
            }
    }

     fun setAdapter(){
        var mAdapter = ArrayAdapter<String>(context, R.layout.simple_list_item_1, Datas.instance.arrForListView)
        Observable.just(mAdapter)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {analysisBinding.analCctvList.adapter = mAdapter}
    }
}