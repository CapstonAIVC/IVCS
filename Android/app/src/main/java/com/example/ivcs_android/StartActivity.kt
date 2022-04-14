package com.example.ivcs_android

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ivcs_android.databinding.ActivityStartBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.viewModel.Msocket
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class StartActivity : AppCompatActivity() {

    companion object{
        lateinit var appContext : Context
    }
    lateinit var startBinding : ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(startBinding.root)
        appContext = applicationContext

        checkPermissions()
    }

    fun setWithInternet(){
        Msocket.instance.setSocket()
        Datas.instance.setInfo()
    }

    fun setBts(){
        startBinding.btGoToStreaming.setOnClickListener {
            if(!Msocket.instance.mSocket.connected()) Msocket.instance.setSocket()
            val intent = Intent(applicationContext,StreamingActivity::class.java)
            if(Datas.instance.arrForUrl.isEmpty()){
//                Datas.instance.setInfo()
                Toast.makeText(this,"서버 연결 불안정",Toast.LENGTH_SHORT).show()
//                Observable.just(0)
//                    .delay(2L,TimeUnit.SECONDS)
//                    .subscribe {
//                        startActivity(intent)
//                    }
            }
            else {
                startActivity(intent)
            }
        }
        startBinding.btGoToAnalysis.setOnClickListener {
            if(!Msocket.instance.mSocket.connected()) Msocket.instance.setSocket()
            val intent = Intent(applicationContext,AnalysisActivity::class.java)
            if(Datas.instance.arrForUrl.isEmpty()){
                Toast.makeText(this,"서버 연결 불안정",Toast.LENGTH_SHORT).show()
            }
            else {
                startActivity(intent)
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
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), 100)

            checkPermissions()
        }
        else{
            setWithInternet()
            setBts()
        }
    }
}