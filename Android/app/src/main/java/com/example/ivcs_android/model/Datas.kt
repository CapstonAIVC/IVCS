package com.example.ivcs_android.model

import android.util.Log
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.security.PublicKey

class Datas {

    companion object{
        var instance = Datas()
    }

    /** 스트리밍 관련 */
    var arrForListView : Array<String> = Array<String>(0){""}
    var arrForUrl : Array<String> = Array<String>(0){""}
    var cctvName = ""
    var cctvIdx = 0
    var changeUrlSubject : PublishSubject<String> = PublishSubject.create()

    /** 교통량 측정 관련 */
    var countSwitchSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var densitySwitchSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var changeCountText : BehaviorSubject<String> = BehaviorSubject.createDefault("차량 수: ")

    /** analysis 관련 */
//    var analysisTypeChange : PublishSubject<Boolean> = PublishSubject.create()
    var analName = ""
    var analIndex = 0
//    var analType = Consts.day
    var startTimeInfo = arrayOf<Long>(2022,4,26,15)
    var endTimeInfo = arrayOf<Long>(2022,4,26,15)
    // 처리중인 요청이 있으면 true
    var analysisDataRequest : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var analysisButtonSubject : PublishSubject<String> = PublishSubject.create()

    fun setInfo(){
        val url = Consts.localhost+Consts.getUrl
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                Log.e("getNamesFailed",e.message.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                var jsonObj = JSONObject(response.body!!.string())
                var jsonArrCctvName = jsonObj.getJSONArray("cctvname")
                var jsonArrUrl = jsonObj.getJSONArray("cctvurl")
                arrForListView = Array(jsonArrCctvName.length()) {""}
                arrForUrl = Array(jsonArrCctvName.length()) {""}
                for( i in 0 until jsonArrCctvName.length()){
                    arrForListView[i] = jsonArrCctvName.getString(i)
                    arrForUrl[i] = jsonArrUrl.getString(i)
                }
            }
        })
    }
}