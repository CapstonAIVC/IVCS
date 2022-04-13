package com.example.ivcs_android.viewModel

import android.util.Log
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.view.streaming.Streaming
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import okhttp3.*
import java.io.IOException

class StreamingBind(streaming : Streaming) {
    var streaming = streaming

    fun initStreamingBind(){
        bindForUrl()
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