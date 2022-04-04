package com.example.ivcs_android

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.ivcs_android.databinding.ActivityMainBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Msocket
import com.example.ivcs_android.view.SetView
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    companion object{
        val Consts = Consts()
    }
    lateinit var btSend : Button
    lateinit var mBinding : ActivityMainBinding

    lateinit var setView : SetView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        checkPermissions()
    }

    fun init(){
        Msocket()
        Msocket.instance.setSocket()

        setView = SetView(this, mBinding)
        setView.setViews()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermissions() {

        var permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            var p : Array<String> = arrayOf(android.Manifest.permission.INTERNET)
            requestPermissions(p,101)
        }
        else{
            Log.e("permission"," Permission granted")
            init()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults.isNotEmpty()) {
//            for (grant in grantResults) {
//                if (grant != PackageManager.PERMISSION_GRANTED)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        checkPermissions()
//                    }
//            }
//        }
//        else{
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions()
        }
    }

}