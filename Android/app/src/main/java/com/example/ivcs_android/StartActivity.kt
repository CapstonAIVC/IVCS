package com.example.ivcs_android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ivcs_android.databinding.ActivityStartBinding
import com.example.ivcs_android.viewModel.Msocket

class StartActivity : AppCompatActivity() {

    var permissionGranted = false
    lateinit var startBinding : ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(startBinding.root)

        checkPermissions()
        Msocket.instance.setSocket()
        setBts()
    }

    fun setBts(){
        startBinding.btGoToStreaming.setOnClickListener {
            if(permissionGranted){
                if(Msocket.instance.mSocket == null) Msocket.instance.setSocket()
                val intent = Intent(applicationContext,StreamingActivity::class.java)
                startActivity(intent)
            }
            else{
                Log.e("btGotoStreaming","permission not granted")
                checkPermissions()
            }
        }
        startBinding.btGoToAnalysis.setOnClickListener {
            if(permissionGranted){
                if(Msocket.instance.mSocket == null) Msocket.instance.setSocket()
                val intent = Intent(applicationContext,AnalysisActivity::class.java)
                startActivity(intent)
            }
            else{
                checkPermissions()
            }
        }
    }

    //퍼미션 체크 및 권한 요청 함수
    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE)
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for(permission in requiredPermissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if(rejectedPermissionList.isNotEmpty()){
            permissionGranted = false
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), 100)
        }
        else{
            permissionGranted = true
        }
    }
}