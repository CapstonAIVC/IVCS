package com.example.ivcs_android.viewModel

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.ivcs_android.AnalysisActivity
import com.example.ivcs_android.streaming.StreamingActivity
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class StartViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    fun btGoToStreamingClicked() {
        if (checkUrlInfo()) {
            val intent = Intent(context, StreamingActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun btGoToAnalysisClicked() {
        if (checkUrlInfo()) {
            val intent = Intent(context, AnalysisActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun checkUrlInfo(): Boolean {
        if (Datas.instance.arrForUrl.isEmpty()) {
            setInfo()
            Toast.makeText(context, "서버 정보 요청중", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    fun setInfo() {
//        val url = Consts.localhost + Consts.getUrl
        val url = Consts.serverMainUrl + Consts.getUrl
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
                Datas.instance.arrForListView = Array(jsonArrCctvName.length()) { "" }
                Datas.instance.arrForUrl = Array(jsonArrCctvName.length()) { "" }
                for (i in 0 until jsonArrCctvName.length()) {
                    Datas.instance.arrForListView[i] = jsonArrCctvName.getString(i)
                    Datas.instance.arrForUrl[i] = jsonArrUrl.getString(i)
                }
                Log.e("urlTest", Datas.instance.arrForUrl[0])
            }
        })
    }
}