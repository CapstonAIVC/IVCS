package com.example.ivcs_android.viewModel

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.ivcs_android.StreamingActivity
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.view.streaming.Streaming
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class StreamingWork(streaming : Streaming, mBinding : ActivityStreamingBinding) {
    var streaming = streaming
    var mBinding = mBinding

    var countData : Disposable = Disposable.empty()

    fun initStreamingBind(){
        bindForUrl()
        bindForSwitch()
        bindchangeCount()
    }

    fun bindchangeCount(){
        Datas.instance.changeCountText
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    mBinding.textViewCounting.text = it
                },
                { Log.e("changeCountTextERR", it.message.toString()) }
            )
    }

    fun bindForSwitch(){
        Datas.instance.countSwitchSubject
//            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if(it){
                        mBinding.textViewCounting.visibility = View.VISIBLE
                        countData = Observable.interval(1500,TimeUnit.MILLISECONDS)
                            .observeOn(Schedulers.io()) // 인터넷 처리를위한 스케쥴러 할당
                            .subscribe (
                                {Msocket.instance.mSocket.emit("req_counting",Datas.instance.cctvIdx)},
                                {
                                    (streaming as Activity).finish()
                                }
                            )
                    }
                    else{
                        countData.dispose()
                        mBinding.textViewCounting.visibility = View.GONE
                    }
                },
                { Log.e("countSubjectErr", it.message.toString())}
            )
        Datas.instance.densitySwitchSubject
//            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if(it){
                        mBinding.imageViewDensity.visibility = View.VISIBLE
                    }
                    else{
                        mBinding.imageViewDensity.visibility = View.GONE
                    }
                },
                { Log.e("densitySubjectErr", it.message.toString())}
            )
    }

    fun bindForUrl(){
        Datas.instance.changeUrlSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    streaming.setPlayerURL(it)
                },
                { Log.e("bindForUrlErr",it.message.toString())}
            )
    }


}