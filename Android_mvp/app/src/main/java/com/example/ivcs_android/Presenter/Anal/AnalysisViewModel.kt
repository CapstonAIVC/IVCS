package com.example.ivcs_android.Presenter.Anal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.ivcs_android.R
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.AnalysisData
import com.example.ivcs_android.model.Datas
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class AnalysisViewModel(context: Context, mBinding: ActivityAnalysisBinding) {

    var context = context
    var analysisBinding = mBinding
    var disposableSetUI: Disposable = Disposable.empty()

    var analysisDataRequest : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var analysisButtonSubject : PublishSubject<String> = PublishSubject.create()

    var dataAnal = AnalysisData()

    fun getBtSubject() : PublishSubject<String>{
        return analysisButtonSubject
    }

    fun analViewModelSetting() {
        analysisBinding.textStart.text =
            dataAnal.startTimeInfo[0].toString() + "년 " + dataAnal.startTimeInfo[1].toString() + "월 " + dataAnal.startTimeInfo[2].toString() + "일 " + dataAnal.startTimeInfo[3].toString() + "시"
        analysisBinding.textEnd.text =
            dataAnal.endTimeInfo[0].toString() + "년 " + dataAnal.endTimeInfo[1].toString() + "월 " + dataAnal.endTimeInfo[2].toString() + "일 " + dataAnal.endTimeInfo[3].toString() + "시"
        analysisBinding.textCctvName.text = "CCTV : " + dataAnal.analName

        setAnalysisRequestBind()
        setAnalysisBtsBind()
    }

    fun setAnalysisBtsBind() {
        analysisButtonSubject
            .subscribe(
                {
                    when (it) {
                        Consts.setStart -> {
                            makeDateDialog(analysisBinding.textStart, true)
                        }
                        Consts.setEnd -> {
                            makeDateDialog(analysisBinding.textEnd, false)
                        }
                        Consts.setCctv -> {
                            makeSelectCctvDialog(analysisBinding.textCctvName)
                        }
                        Consts.requestAnal -> {
                            checkRequestAnalysis()
                        }
                        else -> {
                            // radioBt
                            dataAnal.analType = it
                        }
                    }
                },
                { Log.e("AnalysisBtsErr", it.message.toString()) }
            )
    }

    fun setAnalysisRequestBind() {
        analysisDataRequest
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    if (!it) {
                        // false로 넘어오는 요청은 작업이 끝났다는 신호임
                        disposableSetUI = Disposable.empty()
                    } else {
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
                            .url(Consts.plotUrl)
                            .post(reqBody)
                            .build()
                        var response = client.newCall(request).execute()
                        var resbody = response.body!!.byteString()

                        // response에서 얻은 이미지 가공
                        disposableSetUI = Observable
                            .just(resbody)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.computation())
                            .map {
                                // 여기서 bitmap으로 변환
                                var bArray = it.toByteArray()
                                var bmp = BitmapFactory.decodeByteArray(bArray, 0, bArray.size)
                                val h = analysisBinding.imageAnalysis.layoutParams.height
                                val w = h.toFloat() * (bmp.width.toFloat() / bmp.height.toFloat())
                                Bitmap.createScaledBitmap(bmp, w.toInt(), h, true)
                            }
                            .subscribe(
                                {
                                    analysisBinding.imageAnalysis.setImageBitmap(it)
                                    analysisDataRequest.onNext(false)
                                },
                                {
                                    Log.e("disposableSetUIErr", it.message.toString())
                                    analysisDataRequest.onNext(false)
                                }
                            )
                    }
                },
                { Log.e("AnalysisRequestBindErr", it.message.toString()) }
            )
    }

    fun checkRequestAnalysis() {

        var start =
            dataAnal.startTimeInfo[0] * 1000000 + dataAnal.startTimeInfo[1] * 10000 + dataAnal.startTimeInfo[2] * 100 + dataAnal.startTimeInfo[3]
        var end =
            dataAnal.endTimeInfo[0] * 1000000 + dataAnal.endTimeInfo[1] * 10000 + dataAnal.endTimeInfo[2] * 100 + dataAnal.endTimeInfo[3]
        if (start >= end) {
            Toast.makeText(context, "끝 날자는 시작 날짜 이후여야 합니다.", Toast.LENGTH_SHORT).show()
        } else if (dataAnal.analName == "") {
            Toast.makeText(context, "cctv를 선택해 주세요.", Toast.LENGTH_SHORT).show()
        } else if (analysisDataRequest.value) {
            Toast.makeText(context, "이전 요청이 처리중 입니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 서버에 요청
            analysisDataRequest.onNext(true)
        }
    }

    @SuppressLint("SetTextI18n")
    fun makeDateDialog(textView: TextView, isStartTime: Boolean) {
        val dialog = AlertDialog.Builder(context).create()
        val edialog: LayoutInflater = LayoutInflater.from(context)
        val mView: View = edialog.inflate(R.layout.my_date_pick, null)
        val year: NumberPicker = mView.findViewById(R.id.yearPicker)
        val month: NumberPicker = mView.findViewById(R.id.monthPicker)
        val date: NumberPicker = mView.findViewById(R.id.datePicker)
        val hour: NumberPicker = mView.findViewById(R.id.hourPicker)
        val cancel: Button = mView.findViewById(R.id.btCancel)
        val confirm: Button = mView.findViewById(R.id.btConfirm)

        //  순환 안되게 막기
        year.wrapSelectorWheel = false
//        month.wrapSelectorWheel = false

        //  editText 설정 해제
        year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        date.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        hour.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소값 설정
        year.minValue = 2019
        month.minValue = 1
        date.minValue = 1
        hour.minValue = 0

        //  최대값 설정
        year.maxValue = 2023
        month.maxValue = 12
        date.maxValue = 31
        hour.maxValue = 23

        // 시작 날짜 설정
        if (isStartTime) {
            year.value = dataAnal.startTimeInfo[0].toInt()
            month.value = dataAnal.startTimeInfo[1].toInt()
            date.value = dataAnal.startTimeInfo[2].toInt()
            hour.value = dataAnal.startTimeInfo[3].toInt()
        } else {
            year.value = dataAnal.endTimeInfo[0].toInt()
            month.value = dataAnal.endTimeInfo[1].toInt()
            date.value = dataAnal.endTimeInfo[2].toInt()
            hour.value = dataAnal.endTimeInfo[3].toInt()
        }

        //  취소 버튼 클릭 시
        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        //  완료 버튼 클릭 시
        confirm.setOnClickListener {
            var tmpTimeInfo = arrayOf<Long>(year.value.toLong(),
                month.value.toLong(),
                date.value.toLong(),
                hour.value.toLong())
            textView.text =
                (year.value).toString() + "년 " + (month.value).toString() + "월 " + (date.value).toString() + "일 " + (hour.value).toString() + "시"

            if (isStartTime) {
                dataAnal.startTimeInfo = tmpTimeInfo
            } else {
                dataAnal.endTimeInfo = tmpTimeInfo
            }

            dialog.dismiss()
            dialog.cancel()
        }
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }

    fun makeSelectCctvDialog(textView: TextView) {
        val dialog = AlertDialog.Builder(context).create()
        val edialog: LayoutInflater = LayoutInflater.from(context)
        val mView: View = edialog.inflate(R.layout.my_cctv_pick, null)
        val list: ListView = mView.findViewById(R.id.listViewCctvName)
        val confirm: Button = mView.findViewById(R.id.btConfirmCctv)
        var exText: TextView = mView.findViewById(R.id.textSelected)

        val mAdapter = ArrayAdapter<String>(context,
            android.R.layout.simple_list_item_1,
            Datas.instance.arrForListView)
        list.adapter = mAdapter
        list.setOnItemClickListener { _, _, i, _ ->
            exText.text = Datas.instance.arrForListView[i] + " 가 선택됨"
            dataAnal.analIndex = i
            dataAnal.analName = Datas.instance.arrForListView[i]
        }


        //  완료 버튼 클릭 시
        confirm.setOnClickListener {
            textView.text = "CCTV : " + dataAnal.analName
            dialog.dismiss()
            dialog.cancel()
        }
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }
}