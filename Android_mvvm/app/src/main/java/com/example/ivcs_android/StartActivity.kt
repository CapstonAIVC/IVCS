package com.example.ivcs_android

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ivcs_android.databinding.ActivityStartBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class StartActivity : AppCompatActivity() {

    companion object {
        lateinit var appContext: Context
    }

    lateinit var startBinding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(startBinding.root)
        appContext = applicationContext

        checkPermissions()
    }

    fun setWithInternet() {
        setInfo()
    }

    fun setBts() {
        startBinding.btGoToStreaming.setOnClickListener {
            if (checkInternetResources()) {
                val intent = Intent(applicationContext, StreamingActivity::class.java)
                startActivity(intent)
            }
        }
        startBinding.btGoToAnalysis.setOnClickListener {
            if (checkInternetResources()) {
                val intent = Intent(applicationContext, AnalysisActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkInternetResources(): Boolean {
        if (Datas.instance.arrForUrl.isEmpty()) {
            setInfo()
            Toast.makeText(this, "서버 정보 요청중", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    fun setInfo(){
        val url = Consts.localhost+ Consts.getUrl
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                Log.e("getNamesFailed",e.message.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                var jsonObj = JSONObject(response.body!!.string())
                var jsonArrCctvName = jsonObj.getJSONArray("cctvname")
                var jsonArrUrl = jsonObj.getJSONArray("cctvurl")
                Datas.instance.arrForListView = Array(jsonArrCctvName.length()) {""}
                Datas.instance.arrForUrl = Array(jsonArrCctvName.length()) {""}
                for( i in 0 until jsonArrCctvName.length()){
                    Datas.instance.arrForListView[i] = jsonArrCctvName.getString(i)
                    Datas.instance.arrForUrl[i] = jsonArrUrl.getString(i)
                }
            }
        })
    }

    //퍼미션 체크 및 권한 요청 함수
    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE)
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED
            ) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if (rejectedPermissionList.isNotEmpty()) {
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), 100)

            checkPermissions()
        } else {
            setWithInternet()
            setBts()
        }
    }
}