package com.example.ivcs_android.viewModel

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.AndroidViewModel
import com.example.ivcs_android.AnalysisActivity
import com.example.ivcs_android.R
import com.example.ivcs_android.StreamingActivity
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

class StartViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    init {
        makeMask()
    }

    fun makeMask() {
        val w = context.resources.displayMetrics.widthPixels
        val h = context.resources.displayMetrics.heightPixels/4
        val chojun = AppCompatResources.getDrawable(context, R.drawable.chojun)
            .let {
                var bmp = (it as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
                for (y in 0 until bmp.height) {
                    for (x in 0 until bmp.width) {
                        if (bmp.getPixel(x, y) > Color.GRAY) bmp.setPixel(x, y, Color.alpha(255))
                    }
                }
                Bitmap.createScaledBitmap(bmp, w, h, true)
            }
        val panguo = AppCompatResources.getDrawable(context, R.drawable.panguo)
            .let {
                var bmp = (it as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
                for (x in 0 until bmp.width) {
                    for (y in 0 until bmp.height) {
                        if (bmp.getPixel(x, y) > Color.GRAY) bmp.setPixel(x, y, Color.alpha(255))
                    }
                }
                Bitmap.createScaledBitmap(bmp, w, h, true)
            }
        Datas.instance.maskList.add(chojun)
        Datas.instance.maskList.add(panguo)
    }

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