package com.example.ivcs_android.model

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

class Datas {

    companion object{
        var instance = Datas()
    }

    /** 주소값 관련*/
    var arrForListView : Array<String> = Array<String>(0){""}
    var arrForUrl : Array<String> = Array<String>(0){""}
}


enum class AnalysisEvent {
    ChangeStart, ChangeEnd, ChangeType, ChangeCctv
}