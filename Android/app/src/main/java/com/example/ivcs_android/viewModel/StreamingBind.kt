package com.example.ivcs_android.viewModel

import android.util.Log
import android.view.View
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.view.streaming.Streaming
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class StreamingBind(streaming : Streaming, mBinding : ActivityStreamingBinding) {
    var streaming = streaming
    var mBinding = mBinding

    lateinit var countData : Disposable

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
                    mBinding.textViewCounting.text = "차량 수: $it"
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
                        countData = Observable.interval(1,TimeUnit.SECONDS)
                            .subscribe {
                                Datas.instance.test += 1
                                Datas.instance.changeCountText.onNext(Datas.instance.test)
                            }
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

        Datas.instance.linkArrSubject
            .subscribe(
                {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(it).build()

                    client.newCall(request).enqueue(object: Callback {
                        override fun onFailure(call: Call, e: IOException){
                            //에러 메세지 출력
                            Log.e("testErr",e.message.toString())
                        }
                        override fun onResponse(call: Call, response: Response) {
                            Log.e("testtest", response.request.url.toString())
                            Datas.instance.changeUrlSubject.onNext( response.request.url.toString() )
                        }
                    })
                },{
                    Log.e("linkArrSubjectErr", it.message.toString())
                }
            )
    }
}