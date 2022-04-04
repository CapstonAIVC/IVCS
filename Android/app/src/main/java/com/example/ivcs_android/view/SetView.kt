package com.example.ivcs_android.view

import android.content.Context
import android.widget.Toast
import com.example.ivcs_android.databinding.ActivityMainBinding
import com.example.ivcs_android.model.Msocket

class SetView(context: Context, mBinding: ActivityMainBinding) {

    val context = context
    val mBinding = mBinding

    fun setViews(){
        setBt()
    }

    fun setBt(){
        mBinding.btSend.setOnClickListener {
            if(Msocket.instance.mSocket.connected()) {
                Msocket.instance.mSocket.emit("test", "It is sample")
            }
            else{
                Msocket.instance.setSocket()
                Toast.makeText(context,"소켓 재설정",Toast.LENGTH_SHORT).show()
            }
        }
    }
}