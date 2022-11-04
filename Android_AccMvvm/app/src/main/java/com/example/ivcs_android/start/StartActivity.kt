package com.example.ivcs_android.start

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.ivcs_android.R
import com.example.ivcs_android.Statics
import com.example.ivcs_android.analysis.AnalysisActivity
import com.example.ivcs_android.databinding.ActivityStartBinding
import com.example.ivcs_android.streaming.StreamingActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class StartActivity : AppCompatActivity() {

    lateinit var startBinding: ActivityStartBinding
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        startBinding =
            DataBindingUtil.setContentView<ActivityStartBinding>(this, R.layout.activity_start).apply {
                btGoToAnalysis.setOnClickListener{ btGoToAnalysisClicked() }
                btGoToStreaming.setOnClickListener{ btGoToStreamingClicked() }
            }
        checkPermissions()
    }

    fun btGoToStreamingClicked() {
        if (checkUrlInfo()) {
            val intent = Intent(context, StreamingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun btGoToAnalysisClicked() {
        if (checkUrlInfo()) {
            val intent = Intent(context, AnalysisActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun checkUrlInfo(): Boolean {
        if (Statics.arrForUrl.isEmpty()) {
            setInfo()
            Toast.makeText(context, "서버 정보 요청중", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    fun setInfo() {
//        val url = Consts.localhost + Consts.getUrl
        val url = Statics.serverMainUrl + Statics.getUrl
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("getNamesFailed", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var jsonObj = JSONObject(response.body!!.string())
                var jsonArrCctvName = jsonObj.getJSONArray("cctvname")
                var jsonArrUrl = jsonObj.getJSONArray("cctvurl")
                Statics.arrForListView = Array(jsonArrCctvName.length()) { "" }
                Statics.arrForUrl = Array(jsonArrCctvName.length()) { "" }
                for (i in 0 until jsonArrCctvName.length()) {
                    Statics.arrForListView[i] = jsonArrCctvName.getString(i)
                    Statics.arrForUrl[i] = jsonArrUrl.getString(i)
                }
                Log.e("urlTest", Statics.arrForUrl[0])
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
        }
        else{
            setInfo()
        }
    }
}