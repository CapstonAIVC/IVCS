package com.example.ivcs_android.model

import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.security.PublicKey

class Datas {

    companion object{
        var instance = Datas()
    }

    var changeUrlSubject : PublishSubject<String> = PublishSubject.create()
    var linkArrSubject : PublishSubject<String> = PublishSubject.create()

    var countSwitchSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var densitySwitchSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    var changeCountText : PublishSubject<Int> = PublishSubject.create()
    var test = 0
}