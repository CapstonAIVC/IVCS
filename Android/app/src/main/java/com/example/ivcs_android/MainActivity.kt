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

class MainActivity : AppCompatActivity() {

    companion object{
        val Consts = Consts()
    }
    lateinit var mSocket : Socket

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()

        var btSend : Button = findViewById(R.id.btSend)
        btSend.setOnClickListener {
            Log.e("clicked","dsf")
            mSocket.emit("test","android send")
        }

        setSocket()
    }

    fun setSocket(){
        try {
            mSocket = IO.socket(Consts.tmp)
            mSocket.connect()
            Log.e("Connected", "OK")
        } catch (e: URISyntaxException) {
            Log.e("ERR", e.toString())
        }
        mSocket.on("test") { Log.e("Listen", "test") }
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
    }

}