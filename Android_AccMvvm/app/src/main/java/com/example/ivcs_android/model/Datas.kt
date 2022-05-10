package com.example.ivcs_android.model

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

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
    var analType = Consts.hour
    var analName = ""
    var analIndex = 0
    var startTimeInfo = arrayOf<Long>(2022,4,24,15)
    var endTimeInfo = arrayOf<Long>(2022,4,24,16)
    var analImageHeight = 100
    var changeAnalInfoSubj : PublishSubject<AnalysisEvent> = PublishSubject.create()
}

enum class AnalysisEvent {
    ChangeStart, ChangeEnd, ChangeType, ChangeCctv
}