package com.example.ivcs_android.model

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class Datas {

    companion object{
        var instance = Datas()
    }

    /** CCTV 정보 */
    var arrForListView : Array<String> = Array<String>(0){""}
    var arrForUrl : Array<String> = Array<String>(0){""}

    /** 스트리밍 관련 */
    var cctvName = ""
    var cctvIdx = 0
    var changeUrlSubject : PublishSubject<String> = PublishSubject.create()

    /** 교통량 측정 관련 */
    var countSwitchSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var densitySwitchSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var changeCountText : BehaviorSubject<String> = BehaviorSubject.createDefault("차량 수: ")

//    /** analysis 관련 */
//    var analType = Consts.hour
//    var analName = ""
//    var analIndex = 0
//    var startTimeInfo = arrayOf<Long>(2022,4,24,15)
//    var endTimeInfo = arrayOf<Long>(2022,4,24,16)
//    // 처리중인 요청이 있으면 true
//    var analysisDataRequest : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
//    var analysisButtonSubject : PublishSubject<String> = PublishSubject.create()
}

class DataAnalysis(){
    /** analysis 관련 */
    var analType = Consts.hour
    var analName = Datas.instance.arrForListView[0]
    var analIndex = 0
    var startTimeInfo = arrayOf<Long>(2022,4,24,15)
    var endTimeInfo = arrayOf<Long>(2022,4,24,16)
}