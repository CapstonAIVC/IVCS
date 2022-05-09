package com.example.ivcs_android.viewModel.Streaming

import android.content.Context
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
    }

    fun releaseSocket(){
        mSocket.disconnect()
        mSocket.close()
    }

    fun checkSocket(context : Context) : Boolean{
        if(!instance.mSocket.connected()){
            Toast.makeText(context,"소켓 연결중 입니다.", Toast.LENGTH_SHORT).show()
        }
        return instance.mSocket.connected()
    }

}