package com.example.ivcs_android.model

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class Datas {

    companion object {
        var instance = Datas()
    }

    /** 주소값 관련*/
    var arrForListView: Array<String> = Array<String>(0) { "" }
    var arrForUrl: Array<String> = Array<String>(0) { "" }

    /** 마스크 이미지*/
    var maskList = mutableListOf<Bitmap>()
}

class DataStreaming() {
    /** 스트리밍 관련 */
    var cctvName = ""
    var cctvIdx = -1
    var changeUrlSubject: PublishSubject<String> = PublishSubject.create()

    /** 교통량 측정 관련 */
    var countSwitchSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var debugSwitchSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var changeCountText: BehaviorSubject<String> = BehaviorSubject.createDefault("차량 수: ")
    var changeDensity: PublishSubject<ByteArray> = PublishSubject.create()
    var changeInput: PublishSubject<ByteArray> = PublishSubject.create()
    var textCount: MutableLiveData<String> = MutableLiveData("")
    var textCountShow: MutableLiveData<Boolean> = MutableLiveData(false)
    var debugImageShow: MutableLiveData<Boolean> = MutableLiveData(false)

    /** 스위치 관련*/
    var countingChecked = MutableLiveData<Boolean>().apply { value = false }
    var debugChecked = MutableLiveData<Boolean>().apply { value = false }
    var maskChecked = MutableLiveData<Boolean>().apply { value = false }

    var densityImage: MutableLiveData<Bitmap> = MutableLiveData()
    var inputImage: MutableLiveData<Bitmap> = MutableLiveData()
    val noMaskImage: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply {
        setPixel(0, 0, Color.alpha(255))
    }
    var maskImage: MutableLiveData<Bitmap> = MutableLiveData(noMaskImage)
}

class DataAnal() {
    /** analysis 관련 */
    var analType = Consts.hour
    var analName = ""
    var analIndex = 0
    var startTimeInfo = arrayOf<Long>(2022, 6, 3, 15)
    var endTimeInfo = arrayOf<Long>(2022, 6, 3, 22)
    var analImageHeight = 100
//    var changeAnalInfoSubj : PublishSubject<AnalysisEvent> = PublishSubject.create()

    var startText: MutableLiveData<String> = MutableLiveData("시작 날짜 : ")
    var endText: MutableLiveData<String> = MutableLiveData("종료 날짜 : ")
    var cctvText: MutableLiveData<String> = MutableLiveData("CCTV : ")
    var analImage: MutableLiveData<Bitmap> = MutableLiveData()
}

enum class AnalysisEvent {
    ChangeStart, ChangeEnd, ChangeType, ChangeCctv
}