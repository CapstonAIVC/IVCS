package com.example.ivcs_android.viewModel

import android.util.Log
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class Msocket {

    companion object{
        var instance = Msocket()
    }

    lateinit var mSocket : Socket

    fun setSocket(){
        try {
            mSocket = IO.socket(Consts.localhost)
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("ERR_setsocket", e.toString())
        }

        mSocket.on("cctv") {
            Log.e("Listen", "cctv")
        }

        mSocket.on("hls_res_test") {
//            Datas.instance.changeUrlSubject.onNext(it[0].toString())
            Datas.instance.linkArrSubject.onNext(it[0].toString())
            Log.e("hls 주소", it[0].toString())
        }
    }

}