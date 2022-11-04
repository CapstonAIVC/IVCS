package com.example.ivcs_android.analysis

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.ivcs_android.Statics
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class AnalysisModel() {


    // 처리중인 요청이 있으면 true
    var analysisDataRequest: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false).apply {
        observeOn(Schedulers.io())
        .filter { it } // false는 작업이 끝났음을 명시하는 역할
        .map {
            //json을 만들고 요청을 보낸다.
            // 받은 응답을 mainthread를 이용하는 객체에 넘겨준다.
            // mainthread를 이용하는 객체는 화면을 처리하고 falseㄹ 요청이 끝났음을 subject에 알려준다.
            getAnalysisBitmap()!!
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                it.let { analImage.value = it }
                onNext(false)
            },
            {
                Log.e("disposableSetUIErr", it.message.toString())
                onNext(false)
            }
        )
    }
    /** analysis 관련 */
    var analType = Statics.hour
    var analName = ""
    var analIndex = 0
    var startTimeInfo = arrayOf<Long>(2022,4,24,15)
    var endTimeInfo = arrayOf<Long>(2022,4,24,16)
    var analImageHeight = 100

    var startText: MutableLiveData<String> = MutableLiveData("시작 날짜 : "+ (startTimeInfo[0]).toString() + "년 " + (startTimeInfo[1]).toString() + "월 " + (startTimeInfo[2]).toString() + "일 " + (startTimeInfo[3]).toString() + "시")
    var endText: MutableLiveData<String> = MutableLiveData("종료 날짜 : "+(endTimeInfo[0]).toString() + "년 " + (endTimeInfo[1]).toString() + "월 " + (endTimeInfo[2]).toString() + "일 " + (endTimeInfo[3]).toString() + "시")
    var cctvText: MutableLiveData<String> = MutableLiveData("CCTV : ")
    var analImage: MutableLiveData<Bitmap> = MutableLiveData()


    fun getRequest(): Request {
        var startDate =
            startTimeInfo[0].toString() + "-" + String.format("%02d",
                startTimeInfo[1]) + "-" + String.format("%02d",
                startTimeInfo[2]) + "_" + String.format("%02d",
                startTimeInfo[3])
        var endDate =
            endTimeInfo[0].toString() + "-" + String.format("%02d",
                endTimeInfo[1]) + "-" + String.format("%02d",
                endTimeInfo[2]) + "_" + String.format("%02d",
                endTimeInfo[3])

        var jsonObj = JSONObject()
        jsonObj.put("measure_method", analType)
        jsonObj.put("cameraid", analIndex.toString())
        jsonObj.put("start", startDate)
        jsonObj.put("end", endDate)

        val reqBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonObj.toString()
        )
        return Request.Builder()
            .url(Statics.serverDataUrl+ Statics.plotUrl)
            .post(reqBody)
            .build()
    }

    fun getBitmapFromRequest(request: Request): Bitmap? {
        val client = OkHttpClient()
        val response = client.newCall(request).execute()
        val bArray = response.body!!.byteString().toByteArray()
        val bmp = BitmapFactory.decodeByteArray(bArray, 0, bArray.size)
        val h = analImageHeight
        val w = h.toFloat() * (bmp.width.toFloat() / bmp.height.toFloat())
        return Bitmap.createScaledBitmap(bmp, w.toInt(), h, true)
    }

    fun getAnalysisBitmap(): Bitmap? {
        return getBitmapFromRequest(getRequest())
    }

    fun checkRequestAnalysis(): Boolean {

        var start =
            startTimeInfo[0] * 1000000 + startTimeInfo[1] * 10000 + startTimeInfo[2] * 100 + startTimeInfo[3]
        var end =
            endTimeInfo[0] * 1000000 + endTimeInfo[1] * 10000 + endTimeInfo[2] * 100 + endTimeInfo[3]
        if (start >= end) {
            AnalysisActivity.subjectForToast.onNext("끝 날자는 시작 날짜 이후여야 합니다.")
            return false
        }
        if (analName == "") {
            AnalysisActivity.subjectForToast.onNext("cctv를 선택해 주세요.")
            return false
        } else if (analysisDataRequest.value) {
            AnalysisActivity.subjectForToast.onNext("이전 요청이 처리중 입니다.")
            return false
        }
        return true
    }

}