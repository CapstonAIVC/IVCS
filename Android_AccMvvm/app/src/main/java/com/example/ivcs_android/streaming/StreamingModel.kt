package com.example.ivcs_android.streaming

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StreamingModel {
    var testNum = 1
    val myHeight = MutableLiveData(0)

    /** 스트리밍 관련 */
    var cctvIdx = -1

    /** 교통량 측정 관련 */
    var textCount : MutableLiveData<String> = MutableLiveData("test")
    var textCountShow : MutableLiveData<Boolean> = MutableLiveData(false)
    var densityImageShow : MutableLiveData<Boolean> = MutableLiveData(false)

    var mSocket = Msocket(textCount)
    var countData: Disposable = Disposable.empty()

    fun startCounting(){
        textCountShow.value = true
        startSocket()
    }

    fun stopCounting(){
        textCountShow.value = false
        stopSocket()
    }

    fun startDensity(){
        densityImageShow.value = true
    }

    fun stopDensity(){
        densityImageShow.value = false
    }

    fun startSocket(){
        countData = Observable.interval(1500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()) // 리펙토링시 테스트를 위함
            .map { textCount.value = (++testNum).toString() }
            .observeOn(Schedulers.io()) // 인터넷 처리를위한 스케쥴러 할당
            .subscribe(
                {
                    if(mSocket.mSocket.connected()) mSocket.mSocket.emit("req_counting",cctvIdx)
                },
                {
                    Log.e("socketEmitErr", it.message.toString())
                }
            )
    }

    fun stopSocket(){
        countData.dispose()
    }

    fun checkSocketState():Boolean{
        // 서버를 돌리지 않고 api가 만료되었기 때문에 테스트를 위해 변경함
        return true
        return !mSocket.checkSocket() || cctvIdx == -1
    }

}