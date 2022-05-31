package com.example.ivcs_android.viewModel.streaming

import android.util.Log
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.DataStreaming
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class Msocket(var dataStreaming: DataStreaming) {


    lateinit var mSocket : Socket

    init {
        setSocket()
    }

    private fun setSocket(){
        try {
//            mSocket = IO.socket(Consts.localhost_DataServer)
            mSocket = IO.socket(Consts.serverDataUrl)
            mSocket.connect()
        } catch (e: URISyntaxException) {
            Log.e("ERR_setsocket", e.toString())
        }

        mSocket.on("res_counting") {
            Log.e("res_counting",it[0].toString())
            dataStreaming.changeCountText.onNext( "차량 수: "+it[0].toString() )
        }
    }

    fun releaseSocket(){
        mSocket.disconnect()
        mSocket.close()
    }

    fun checkSocket() : Boolean{
//        if(mSocket.connected()){
//            Toast.makeText(context,"소켓 연결중 입니다.", Toast.LENGTH_SHORT).show()
//        }
        return mSocket.connected()
    }

}