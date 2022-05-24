package com.example.ivcs_android.viewModel.analysis

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.DataAnal
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class AnalysisViewModel(application: Application, fragmentManager: FragmentManager) :
    AndroidViewModel(application) {

    var dataAnal = DataAnal()

    var fragmentManager = fragmentManager
    private val context: Context = getApplication<Application>().applicationContext
    var disposableSetUI: Disposable = Disposable.empty()

    // 처리중인 요청이 있으면 true
    var analysisDataRequest: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    init {
        bindAnalysisRequest()
//        bindChangeAnalInfoSubj()
    }

    fun radioClicked(view: View) {
        dataAnal.analType = view.tag as String
    }

    fun requestBtClicked() {
        if (checkRequestAnalysis()) {
            analysisDataRequest.onNext(true)
        }
    }

    //    fun bindAnalysisRequest() {
//        analysisDataRequest
//            .observeOn(Schedulers.io())
//            .subscribe(
//                {
//                    if (!it) {
//                        disposableSetUI = Disposable.empty()
//                    } else {
//                        //json을 만들고 요청을 보낸다.
//                        // 받은 응답을 mainthread를 이용하는 객체에 넘겨준다.
//                        // mainthread를 이용하는 객체는 화면을 처리하고 falseㄹ 요청이 끝났음을 subject에 알려준다.
//                        var startDate =
//                            dataAnal.startTimeInfo[0].toString() + "-" + String.format("%02d",
//                                dataAnal.startTimeInfo[1]) + "-" + String.format("%02d",
//                                dataAnal.startTimeInfo[2]) + "_" + String.format("%02d",
//                                dataAnal.startTimeInfo[3])
//                        var endDate =
//                            dataAnal.endTimeInfo[0].toString() + "-" + String.format("%02d",
//                                dataAnal.endTimeInfo[1]) + "-" + String.format("%02d",
//                                dataAnal.endTimeInfo[2]) + "_" + String.format("%02d",
//                                dataAnal.endTimeInfo[3])
//
//                        var jsonObj = JSONObject()
//                        jsonObj.put("measure_method", dataAnal.analType)
//                        jsonObj.put("cameraid", dataAnal.analIndex.toString())
//                        jsonObj.put("start", startDate)
//                        jsonObj.put("end", endDate)
//
//                        var reqBody = RequestBody.create(
//                            "application/json; charset=utf-8".toMediaTypeOrNull(),
//                            jsonObj.toString()
//                        )
//
//                        // 그림 요청
//                        var client = OkHttpClient()
//                        var request = Request.Builder()
//                            .url(Consts.plotUrl)
//                            .post(reqBody)
//                            .build()
//                        var response = client.newCall(request).execute()
//                        var resbody = response.body!!.byteString()
////
//                        // response에서 얻은 이미지 가공
//                        disposableSetUI = Observable
//                            .just(resbody)
//                            .observeOn(Schedulers.io())
//                            .map {
//                                // 여기서 bitmap으로 변환
//                                var bArray = it.toByteArray()
////                                Log.e("bArrSize", bArray.size.toString())
//                                var bmp = BitmapFactory.decodeByteArray(bArray, 0, bArray.size)
////                                var bmp: Bitmap = BitmapFactory.decodeResource(context.resources,
////                                    R.drawable.graph)
//                                val h = dataAnal.analImageHeight
//                                val w = h.toFloat() * (bmp.width.toFloat() / bmp.height.toFloat())
//                                Bitmap.createScaledBitmap(bmp, w.toInt(), h, true)
//                            }
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                {
//                                    dataAnal.analImage.value = it
//                                    analysisDataRequest.onNext(false)
//                                },
//                                {
//                                    Log.e("disposableSetUIErr", it.message.toString())
//                                    analysisDataRequest.onNext(false)
//                                }
//                            )
//                    }
//
//                },
//                { Log.e("AnalysisRequestBindErr", it.message.toString()) }
//            )
//    }
    fun bindAnalysisRequest() {
        analysisDataRequest
            .observeOn(Schedulers.io())
            .filter { it } // false는 작업이 끝났음을 명시하는 역할
            .map {
                //json을 만들고 요청을 보낸다.
                // 받은 응답을 mainthread를 이용하는 객체에 넘겨준다.
                // mainthread를 이용하는 객체는 화면을 처리하고 falseㄹ 요청이 끝났음을 subject에 알려준다.
                var startDate =
                    dataAnal.startTimeInfo[0].toString() + "-" + String.format("%02d",
                        dataAnal.startTimeInfo[1]) + "-" + String.format("%02d",
                        dataAnal.startTimeInfo[2]) + "_" + String.format("%02d",
                        dataAnal.startTimeInfo[3])
                var endDate =
                    dataAnal.endTimeInfo[0].toString() + "-" + String.format("%02d",
                        dataAnal.endTimeInfo[1]) + "-" + String.format("%02d",
                        dataAnal.endTimeInfo[2]) + "_" + String.format("%02d",
                        dataAnal.endTimeInfo[3])

                var jsonObj = JSONObject()
                jsonObj.put("measure_method", dataAnal.analType)
                jsonObj.put("cameraid", dataAnal.analIndex.toString())
                jsonObj.put("start", startDate)
                jsonObj.put("end", endDate)

                var reqBody = RequestBody.create(
                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                    jsonObj.toString()
                )

                // 그림 요청
                var client = OkHttpClient()
                var request = Request.Builder()
                    .url(Consts.serverDataUrl+Consts.plotUrl)
                    .post(reqBody)
                    .build()
                var response = client.newCall(request).execute()
                var resbody = response.body!!.byteString()
//
                // response에서 얻은 이미지 가공
                // 여기서 bitmap으로 변환
                var bArray = resbody.toByteArray()
                var bmp = BitmapFactory.decodeByteArray(bArray, 0, bArray.size)
                val h = dataAnal.analImageHeight
                val w = h.toFloat() * (bmp.width.toFloat() / bmp.height.toFloat())
                Bitmap.createScaledBitmap(bmp, w.toInt(), h, true)

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    it.let { dataAnal.analImage.value = it }
                    analysisDataRequest.onNext(false)
                },
                {
                    Log.e("disposableSetUIErr", it.message.toString())
                    analysisDataRequest.onNext(false)
                }
            )
    }

    fun checkRequestAnalysis(): Boolean {

        var start =
            dataAnal.startTimeInfo[0] * 1000000 + dataAnal.startTimeInfo[1] * 10000 + dataAnal.startTimeInfo[2] * 100 + dataAnal.startTimeInfo[3]
        var end =
            dataAnal.endTimeInfo[0] * 1000000 + dataAnal.endTimeInfo[1] * 10000 + dataAnal.endTimeInfo[2] * 100 + dataAnal.endTimeInfo[3]
        if (start >= end) {
            Toast.makeText(context, "끝 날자는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (dataAnal.analName == "") {
            Toast.makeText(context, "cctv를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            return false
        } else if (analysisDataRequest.value) {
            Toast.makeText(context, "이전 요청이 처리중 입니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    fun clickStartText() {
        var dial = DatePickDialogFragment(dataAnal)
        dial.arguments = Bundle().also { it.putBoolean("isStart", true) }
        dial.show(fragmentManager, "DatePickDialogFragment")
    }

    fun clickEndText() {
        var dial = DatePickDialogFragment(dataAnal)
        dial.arguments = Bundle().also { it.putBoolean("isStart", false) }
        dial.show(fragmentManager, "DatePickDialogFragment")
    }

    fun cctvNameClicked() {
        var dial = CctvPickDialogFragment(dataAnal)
        dial.show(fragmentManager, "CctvPickDialogFragment")
    }
}