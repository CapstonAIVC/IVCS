package com.example.ivcs_android.viewModel.streaming

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Switch
import androidx.lifecycle.AndroidViewModel
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.DataStreaming
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StreamingViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = getApplication<Application>().applicationContext

    var dataStreaming = DataStreaming()
    var mSocket = Msocket(dataStreaming)
    var myPlayer: MyPlayer = MyPlayer(context)
    var countData: Disposable = Disposable.empty()

    init {
        bindForUrl()
        bindForSwitch()
        bindchangeCount()
    }

    fun bindchangeCount() {
        dataStreaming.changeCountText
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    dataStreaming.textCount.value = it
                },
                { Log.e("changeCountTextERR", it.message.toString()) }
            )
    }

    fun bindForSwitch() {
        dataStreaming.countSwitchSubject
            .subscribe(
                {
                    if (it) {
                        dataStreaming.textCountShow.value = true
                        countData = Observable.interval(1500, TimeUnit.MILLISECONDS)
                            .observeOn(Schedulers.io()) // 인터넷 처리를위한 스케쥴러 할당
                            .subscribe(
                                {
                                    mSocket.mSocket.emit("req_counting",
                                        dataStreaming.cctvIdx)
                                },
                                {
                                    Log.e("socketEmitErr", it.message.toString())
                                }
                            )
                    } else {
                        countData.dispose()
                        dataStreaming.textCountShow.value = false
                    }
                },
                { Log.e("countSubjectErr", it.message.toString()) }
            )
        dataStreaming.densitySwitchSubject
//            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    dataStreaming.densityImageShow.value = it
                },
                { Log.e("densitySubjectErr", it.message.toString()) }
            )
    }

    fun bindForUrl() {
        dataStreaming.changeUrlSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    myPlayer.setPlayerURL(it)
//                    myPlayer.setPlayerURL(Consts.hlstest)
                },
                { Log.e("bindForUrlErr", it.message.toString()) }
            )
    }

    fun clickCountSwitch(view: View) {
        if (!mSocket.checkSocket() || dataStreaming.cctvIdx == -1) {
            (view as Switch).isChecked = false
        } else {
            dataStreaming.countSwitchSubject.onNext((view as Switch).isChecked)
        }
    }

    fun clickDensitySwitch(view: View) {
        dataStreaming.densitySwitchSubject.onNext((view as Switch).isChecked)
    }

}