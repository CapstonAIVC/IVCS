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

        mSocket.on("res_counting") {
            Log.e("Listen", "res_counting")
            Datas.instance.changeCountText.onNext( "차량 수: "+it[0].toString() )
        }
    }

}