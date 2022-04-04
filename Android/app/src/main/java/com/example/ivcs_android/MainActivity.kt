package com.example.ivcs_android

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object{
        val Consts = Consts()
    }
    lateinit var mSocket : Socket
    lateinit var btSend : Button

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        setSocket()
    }

    fun setBt(){
        btSend= findViewById(R.id.btSend)
        btSend.setOnClickListener {
            Log.e("clicked","dsf")
            mSocket.emit("test","android send")
        }
    }

    fun setSocket(){
        try {
//            mSocket = IO.socket(Consts.localhost)
            mSocket = IO.socket("http://10.0.2.2:3000")
            mSocket.connect()
            Log.e("Connected?", mSocket.connected().toString())
            mSocket.on("test") { Log.e("Listen", "test") }
            setBt()
        } catch (e: URISyntaxException) {
            Log.e("ERR_setsocket", e.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermissions() {

        var permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            var p : Array<String> = arrayOf(android.Manifest.permission.INTERNET)
            requestPermissions(p,101)
//            if (shouldShowRequestPermissionRationale()) {
//                //권한 설명 필요
//            } else {
//                requestPermission(this, new String[] {permission}, 101);
//            }
        }
        else{
            Log.e("permission"," Permission granted")
        }
    }

}