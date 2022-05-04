package com.example.ivcs_android.viewModel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.ivcs_android.DatePickDialogFragment
import com.example.ivcs_android.R
import com.example.ivcs_android.model.AnalysisEvent
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = getApplication<Application>().applicationContext
    var disposableSetUI: Disposable = Disposable.empty()

    var startText: MutableLiveData<String> = MutableLiveData("시작 날짜 : ")
    var endText: MutableLiveData<String> = MutableLiveData("종료 날짜 : ")
    var cctvText: MutableLiveData<String> = MutableLiveData("CCTV : ")
    var analImage: MutableLiveData<Bitmap> = MutableLiveData()

    // 처리중인 요청이 있으면 true
    var analysisDataRequest: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    init {
        bindAnalysisRequest()
        bindChangeAnalInfoSubj()
    }

//    fun startDateClicked() {
////        makeDateDialog(true)
//    }
//
//    fun endDateClicked() {
////        makeDateDialog(false)
//    }

    fun cctvNameClicked() {
        makeSelectCctvDialog()
    }

    fun radioClicked(view: View) {
        Datas.instance.analType = view.tag as String
    }

    fun requestBtClicked() {
        if (checkRequestAnalysis()) {
            analysisDataRequest.onNext(true)
        }
    }

    fun bindChangeAnalInfoSubj() {
        Datas.instance.changeAnalInfoSubj
            .subscribe(
                {
                    when (it) {
                        AnalysisEvent.ChangeStart -> {
                            startText.value =
                                (Datas.instance.startTimeInfo[0]).toString() + "년 " + (Datas.instance.startTimeInfo[1]).toString() + "월 " + (Datas.instance.startTimeInfo[2]).toString() + "일 " + (Datas.instance.startTimeInfo[3]).toString() + "시"
                        }
                        AnalysisEvent.ChangeEnd -> {
                            endText.value =
                                (Datas.instance.endTimeInfo[0]).toString() + "년 " + (Datas.instance.endTimeInfo[1]).toString() + "월 " + (Datas.instance.endTimeInfo[2]).toString() + "일 " + (Datas.instance.endTimeInfo[3]).toString() + "시"
                        }
                        AnalysisEvent.ChangeCctv -> {}
                        AnalysisEvent.ChangeType -> {}
                    }
                },
                { Log.e("changeErr", it.message.toString()) }
            )
    }

    fun bindAnalysisRequest() {
        analysisDataRequest
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    if (!it) {
                        disposableSetUI = Disposable.empty()
                    } else {
                        //json을 만들고 요청을 보낸다.
                        // 받은 응답을 mainthread를 이용하는 객체에 넘겨준다.
                        // mainthread를 이용하는 객체는 화면을 처리하고 falseㄹ 요청이 끝났음을 subject에 알려준다.
                        var startDate =
                            Datas.instance.startTimeInfo[0].toString() + "-" + String.format("%02d",
                                Datas.instance.startTimeInfo[1]) + "-" + String.format("%02d",
                                Datas.instance.startTimeInfo[2]) + "_" + String.format("%02d",
                                Datas.instance.startTimeInfo[3])
                        var endDate =
                            Datas.instance.endTimeInfo[0].toString() + "-" + String.format("%02d",
                                Datas.instance.endTimeInfo[1]) + "-" + String.format("%02d",
                                Datas.instance.endTimeInfo[2]) + "_" + String.format("%02d",
                                Datas.instance.endTimeInfo[3])

                        var jsonObj = JSONObject()
                        jsonObj.put("measure_method", Datas.instance.analType)
                        jsonObj.put("cameraid", Datas.instance.analIndex.toString())
                        jsonObj.put("start", startDate)
                        jsonObj.put("end", endDate)

                        var reqBody = RequestBody.create(
                            "application/json; charset=utf-8".toMediaTypeOrNull(),
                            jsonObj.toString()
                        )

                        // 그림 요청
                        var client = OkHttpClient()
                        var request = Request.Builder()
                            .url(Consts.plotUrl)
                            .post(reqBody)
                            .build()
                        var response = client.newCall(request).execute()
                        var resbody = response.body!!.byteString()
//
                        // response에서 얻은 이미지 가공
                        disposableSetUI = Observable
                            .just(resbody)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.computation())
                            .map {
                                // 여기서 bitmap으로 변환
                                var bArray = it.toByteArray()
//                                Log.e("bArrSize", bArray.size.toString())
                                var bmp = BitmapFactory.decodeByteArray(bArray, 0, bArray.size)
//                                var bmp: Bitmap = BitmapFactory.decodeResource(context.resources,
//                                    R.drawable.graph)
                                val h = Datas.instance.analImageHeight
                                val w = h.toFloat() * (bmp.width.toFloat() / bmp.height.toFloat())
                                Bitmap.createScaledBitmap(bmp, w.toInt(), h, true)
                            }
                            .subscribe(
                                {
                                    analImage.value = it
                                    analysisDataRequest.onNext(false)
                                },
                                { Log.e("disposableSetUIErr", it.message.toString()) }
                            )
                    }

                },
                { Log.e("AnalysisRequestBindErr", it.message.toString()) }
            )
    }

    fun checkRequestAnalysis(): Boolean {

        var start =
            Datas.instance.startTimeInfo[0] * 1000000 + Datas.instance.startTimeInfo[1] * 10000 + Datas.instance.startTimeInfo[2] * 100 + Datas.instance.startTimeInfo[3]
        var end =
            Datas.instance.endTimeInfo[0] * 1000000 + Datas.instance.endTimeInfo[1] * 10000 + Datas.instance.endTimeInfo[2] * 100 + Datas.instance.endTimeInfo[3]
        if (start >= end) {
            Toast.makeText(context, "끝 날자는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (Datas.instance.analName == "") {
            Toast.makeText(context, "cctv를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            return false
        } else if (analysisDataRequest.value) {
            Toast.makeText(context, "이전 요청이 처리중 입니다.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    fun makeSelectCctvDialog() {
        val dialog = AlertDialog.Builder(context).create()
        val edialog: LayoutInflater = LayoutInflater.from(context)
        val mView: View = edialog.inflate(R.layout.cctv_pick, null)
        val list: ListView = mView.findViewById(R.id.listViewCctvName)
        val confirm: Button = mView.findViewById(R.id.btConfirmCctv)
        var exText: TextView = mView.findViewById(R.id.textSelected)

        val mAdapter = ArrayAdapter<String>(context,
            android.R.layout.simple_list_item_1,
            Datas.instance.arrForListView)

        var index = Datas.instance.analIndex

        list.adapter = mAdapter
        list.setOnItemClickListener { _, _, i, _ ->
            exText.text = Datas.instance.arrForListView[i] + " 가 선택됨"
            index = i
        }


        //  완료 버튼 클릭 시
        confirm.setOnClickListener {
            Datas.instance.analIndex = index
            Datas.instance.analName = Datas.instance.arrForListView[index]
            cctvText.value = "CCTV : " + Datas.instance.analName
            dialog.dismiss()
            dialog.cancel()
        }
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }
}