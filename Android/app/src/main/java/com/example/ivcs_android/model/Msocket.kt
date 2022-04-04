package com.example.ivcs_android.model

import android.util.Log
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
//            mSocket = IO.socket(Consts.localhost)
            mSocket = IO.socket("http://10.0.2.2:3000")
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("ERR_setsocket", e.toString())
        }

        mSocket.on("cctv") {
            Log.e("Listen", "cctv")
        }
    }

}