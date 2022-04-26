package com.example.ivcs_android.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.ivcs_android.R
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Datas

class AnalysisBind(context: Context, mBinding: ActivityAnalysisBinding) {

    var context = context
    var analysisBinding = mBinding

    fun bindForAnalysis(){
        setAnalysisRequestBind()
//        setAdapter()
//        bindForChangeAnalData()
    }

    fun setAnalysisRequestBind(){
        Datas.instance.analysisDataRequest
            .filter { it } // false로 넘어오는 요청은 작업이 끝났다는 신호임
            .subscribe(
                {
                // 요청 및 적용 후 false로 요청처리가 끝났음을 명시
                    var bmp : Bitmap = BitmapFactory.decodeResource( context.resources, R.drawable.graph )
                    val h = analysisBinding.imageAnalysis.layoutParams.height
                    val w = h.toFloat() * (bmp.width.toFloat()/bmp.height.toFloat())
                    bmp = Bitmap.createScaledBitmap(bmp, w.toInt(), h, true )
                    analysisBinding.imageAnalysis.setImageBitmap(bmp)

                    Datas.instance.analysisDataRequest.onNext(false)
                },
                { Log.e("AnalysisRequestBindErr", it.message.toString())}
            )
    }

//    fun bindForChangeAnalData(){
//        // 정보 요청
//        Datas.instance.analysisTypeChange
//            .subscribe(
//                {
//                    Msocket.instance.checkSocket(context as Activity)
//                    var str = "{\"cctvname\":\"${Datas.instance.analName}\",\"type\":\"${Datas.instance.analType}\"}"
//                    Msocket.instance.mSocket.emit("req_analysis", str )
//                },
//                {Log.e("analysisTypeChangErr",it.message.toString())}
//            )
//        // 들어온 정보 처리
//        Datas.instance.analysisDataChange
//            .subscribe(
//                {
//                    var jsonObj = JSONObject(it)
//                    var objArr = jsonObj.getJSONArray("data")
//                    var data = arrayListOf<ArrayList<Int>>(ArrayList())
//                    for( i in 0 until objArr.length()){
//                        data[0].add( objArr[i] as Int)
//                    }
//                    var bottom : ArrayList<String> = when( jsonObj.get("type") ){
//                        Consts.day -> { Consts.bottomDay }
//                        Consts.month -> { Consts.bottomMonth }
//                        else -> { Consts.bottomYear }
//                    }
//                    setLineView(data,bottom)
//                },
//                { Log.e("analysisDataChangeErr",it.message.toString()) }
//            )
//    }

//    fun setLineView(data : ArrayList<ArrayList<Int>>, bottom : ArrayList<String>){
//        Observable.just(1)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                analysisBinding.lineView.setBottomTextList(bottom)
//                analysisBinding.lineView.setDataList(data) //or lineView.setFloatDataList(floatDataLists)
//            }
//    }
//
//     fun setAdapter(){
//        var mAdapter = ArrayAdapter<String>(context, R.layout.simple_list_item_1, Datas.instance.arrForListView)
//        Observable.just(mAdapter)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {analysisBinding.analCctvList.adapter = mAdapter}
//    }
}