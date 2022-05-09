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

}

class DataAnalysis(){
    /** analysis 관련 */
    var analType = Consts.hour
    var analName = Datas.instance.arrForListView[0]
    var analIndex = 0
    var startTimeInfo = arrayOf<Long>(2022,4,24,15)
    var endTimeInfo = arrayOf<Long>(2022,4,24,16)
}

class DataStreaming(){
    /** Streaming 관련 */
    var cctvName = ""
    var cctvIdx = 0
}