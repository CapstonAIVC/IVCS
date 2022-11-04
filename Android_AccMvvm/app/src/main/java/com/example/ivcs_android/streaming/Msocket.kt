package com.example.ivcs_android.streaming

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.ivcs_android.Statics
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class Msocket(val textCount: MutableLiveData<String>) {


    lateinit var mSocket : Socket
    var dispose = Disposable.empty()

    init {
        setSocket()
    }

    private fun setSocket(){
        try {
            mSocket = IO.socket(Statics.serverDataUrl)
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("ERR_setsocket", e.toString())
        }

        mSocket.on("res_counting") {
            Log.e("res_counting",it[0].toString())

            dispose = Observable.just("차량 수: "+it[0].toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({textCount.value = it})
        }
    }

    fun releaseSocket(){
        mSocket.disconnect()
        mSocket.close()
    }

    fun checkSocket() : Boolean{
        return mSocket.connected()
    }

}