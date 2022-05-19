package com.example.ivcs_android.Presenter.Streaming

import android.util.Log
import android.view.View
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.StreamingData
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.view.streaming.Streaming
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class StreamingBind(streaming: Streaming, mBinding: ActivityStreamingBinding) {
    var streaming = streaming
    var mBinding = mBinding
    var dataStreaming = StreamingData()

    var countData: Disposable = Disposable.empty()

    var eventSubject: PublishSubject<Int> = PublishSubject.create()

    var changeUrlSubject: PublishSubject<String> = PublishSubject.create()
    var countSwitchSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var densitySwitchSubject: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var changeCountText: BehaviorSubject<String> = BehaviorSubject.createDefault("차량 수: ")

    lateinit var mSocket: Msocket

    fun initStreamingBind() {
        mSocket = Msocket().also { it.setSocket(changeCountText) }
        bindForUrl()
        bindForSwitch()
        bindchangeCount()
        bindEventSubject()
    }

    fun bindEventSubject() {
        eventSubject
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    when (it) {
                        Consts.switchDensity -> {
                            densitySwitchSubject.onNext(mBinding.densitySwitch.isChecked)
                        }
                        Consts.switchCounting -> {
                            countSwitchSubject.onNext(mBinding.countingSwitch.isChecked)
                        }
                        else -> {
                            dataStreaming.cctvIdx = it
                            dataStreaming.cctvName = Datas.instance.arrForListView[it]
                            changeUrlSubject.onNext(Datas.instance.arrForUrl[it])
                        }
                    }
                },
                { Log.e("StreamingEventSubjErr", it.message.toString()) }
            )
    }

    fun bindchangeCount() {
        changeCountText
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mBinding.textViewCounting.text = it
                },
                { Log.e("changeCountTextERR", it.message.toString()) }
            )
    }

    fun bindForSwitch() {
        countSwitchSubject
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                if (it && (!mSocket.mSocket.connected() || dataStreaming.cctvName == "")) {
                    Log.e("switchErr", "소켓연결 불량 또는 cctv 설정 안함")
                    false
                } else {
                    it
                }
            }
            .subscribe(
                {
                    if (it) {
                        mBinding.textViewCounting.visibility = View.VISIBLE
                        countData = Observable.interval(1500, TimeUnit.MILLISECONDS)
                            .observeOn(Schedulers.io()) // 인터넷 처리를위한 스케쥴러 할당
                            .subscribe(
                                {
                                    mSocket.mSocket.emit("req_counting",
                                        dataStreaming.cctvIdx)
                                },
                                {
                                    Log.e("countErr", it.message.toString())
                                }
                            )
                    } else {
                        countData.dispose()
                        mBinding.countingSwitch.isChecked = false
                        mBinding.textViewCounting.visibility = View.GONE
                    }
                },
                { Log.e("countSubjectErr", it.message.toString()) }
            )
        densitySwitchSubject
//            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it) {
                        mBinding.imageViewDensity.visibility = View.VISIBLE
                    } else {
                        mBinding.imageViewDensity.visibility = View.GONE
                    }
                },
                { Log.e("densitySubjectErr", it.message.toString()) }
            )
    }

    fun bindForUrl() {
        changeUrlSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    streaming.setPlayerURL(it)
                },
                { Log.e("bindForUrlErr", it.message.toString()) }
            )
    }

}