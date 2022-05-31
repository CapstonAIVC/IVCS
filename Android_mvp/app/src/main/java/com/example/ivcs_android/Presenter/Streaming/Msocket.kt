package com.example.ivcs_android.Presenter.Streaming

import android.util.Log
import com.example.ivcs_android.model.Consts
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class Msocket {

    lateinit var mSocket : Socket
    lateinit var textChangeEvent : BehaviorSubject<String>

    fun setSocket(changeCountTextSubj : BehaviorSubject<String>){
        textChangeEvent = changeCountTextSubj
        try {
            mSocket = IO.socket(Consts.localhost_DataServer)
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("ERR_setsocket", e.toString())
        }

        mSocket.on("res_counting") {
            Log.e("Listen", "res_counting")
            textChangeEvent.onNext( "차량 수: "+it[0].toString() )
        }
    }

    fun releaseSocket(){
        mSocket.disconnect()
        mSocket.close()
    }

}