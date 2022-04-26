package com.example.ivcs_android.viewModel

import android.app.Activity
import android.util.Log
import android.widget.Toast
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
            mSocket = IO.socket(Consts.localhost_DataServer)
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("ERR_setsocket", e.toString())
        }

        mSocket.on("res_counting") {
            Log.e("Listen", "res_counting")
            Datas.instance.changeCountText.onNext( "차량 수: "+it[0].toString() )
        }

//        mSocket.on("res_analysis") {
//            Log.e("Listen", "res_analysis")
//            Datas.instance.analysisDataChange.onNext(it[0].toString())
//        }
    }

    fun checkSocket(activity : Activity){
        if(!instance.mSocket.connected()){
            Toast.makeText(activity.applicationContext,"소켓 연결 해제됨", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
    }

}