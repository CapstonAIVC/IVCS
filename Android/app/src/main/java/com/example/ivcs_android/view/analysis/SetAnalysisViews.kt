package com.example.ivcs_android.view.analysis

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.example.ivcs_android.R
import com.example.ivcs_android.databinding.ActivityAnalysisBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import org.w3c.dom.Text

class SetAnalysisViews(context: Context, mBinding: ActivityAnalysisBinding) {

    /*
    * AnalysisActivity의 cctv선택, 그래프 규격 선택 등을 담당
    * */

    var context = context
    var binding = mBinding

    fun setViews(){
        setTextBts()
        setBts()
    }

    fun setTextBts(){
        binding.textStart.setOnClickListener {
            makeDateDialog(binding.textStart,true)
        }
        binding.textEnd.setOnClickListener {
            makeDateDialog(binding.textEnd,false)
        }
        binding.textCctvName.setOnClickListener {
            makeSelectCctvDialog( binding.textCctvName )
        }
    }

    fun setBts(){
        binding.btRequest.setOnClickListener {
            var start = Datas.instance.startTimeInfo[0]*1000000+Datas.instance.startTimeInfo[1]*10000+Datas.instance.startTimeInfo[2]*100+Datas.instance.startTimeInfo[3]
            var end = Datas.instance.endTimeInfo[0]*1000000+Datas.instance.endTimeInfo[1]*10000+Datas.instance.endTimeInfo[2]*100+Datas.instance.endTimeInfo[3]
            if(start >= end){
                Toast.makeText(context,"끝 날자는 시작 날짜 이후여야 합니다.",Toast.LENGTH_SHORT).show()
            }
            else if( Datas.instance.analName == "" ){
                Toast.makeText(context,"cctv를 선택해 주세요.",Toast.LENGTH_SHORT).show()
            }
            else if( Datas.instance.analysisDataRequest.value ){
                Toast.makeText(context,"이전 요청이 처리중 입니다.",Toast.LENGTH_SHORT).show()
            }
            else{
                // 서버에 요청
                Datas.instance.analysisDataRequest.onNext(true)
            }
        }
    }
//
//    fun setRadioBts(){
//
//        binding.rBtDay.tag = Consts.day
//        binding.rBtMonth.tag = Consts.month
//        binding.rBtYear.tag = Consts.year
//
//        binding.rBtGroup.setOnCheckedChangeListener { radioGroup, checkedViewId ->
//            Datas.instance.analType = binding.root.findViewById<RadioButton>(checkedViewId).tag as String
//            Datas.instance.analysisTypeChange.onNext(true)
//        }
//    }
//
//    fun setListView(){
//        binding.analCctvList.setOnItemClickListener { _,_,i,_ ->
//            Datas.instance.analName = Datas.instance.arrForListView[i]
//            Datas.instance.analysisTypeChange.onNext(true)
//        }
//    }
@SuppressLint("SetTextI18n")
fun makeDateDialog(textView : TextView, isStartTime : Boolean){
        val dialog = AlertDialog.Builder(context).create()
        val edialog : LayoutInflater = LayoutInflater.from(context)
        val mView : View = edialog.inflate(R.layout.my_date_pick,null)
        val year : NumberPicker = mView.findViewById(R.id.yearPicker)
        val month : NumberPicker = mView.findViewById(R.id.monthPicker)
        val date : NumberPicker = mView.findViewById(R.id.datePicker)
        val hour : NumberPicker = mView.findViewById(R.id.hourPicker)
        val cancel : Button = mView.findViewById(R.id.btCancel)
        val confirm : Button = mView.findViewById(R.id.btConfirm)

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
        if(isStartTime){
            year.value = Datas.instance.startTimeInfo[0].toInt()
            month.value = Datas.instance.startTimeInfo[1].toInt()
            date.value = Datas.instance.startTimeInfo[2].toInt()
            hour.value = Datas.instance.startTimeInfo[3].toInt()
        }
        else{
            year.value = Datas.instance.endTimeInfo[0].toInt()
            month.value = Datas.instance.endTimeInfo[1].toInt()
            date.value = Datas.instance.endTimeInfo[2].toInt()
            hour.value = Datas.instance.endTimeInfo[3].toInt()
        }

        //  취소 버튼 클릭 시
        cancel.setOnClickListener {
            dialog.dismiss()
            dialog.cancel()
        }
        //  완료 버튼 클릭 시
        confirm.setOnClickListener {
            var tmpTimeInfo = arrayOf<Long>(year.value.toLong(),month.value.toLong(),date.value.toLong(),hour.value.toLong())
            textView.text = (year.value).toString() + "년 " + (month.value).toString() + "월 " + (date.value).toString() + "일 " + (hour.value).toString() + "시"

            if(isStartTime){
                Datas.instance.startTimeInfo = tmpTimeInfo
            }
            else{
                Datas.instance.endTimeInfo = tmpTimeInfo
            }

            dialog.dismiss()
            dialog.cancel()
        }
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }

    fun makeSelectCctvDialog( textView : TextView ){
        val dialog = AlertDialog.Builder(context).create()
        val edialog : LayoutInflater = LayoutInflater.from(context)
        val mView : View = edialog.inflate(R.layout.my_cctv_pick,null)
        val list : ListView = mView.findViewById(R.id.listViewCctvName)
        val confirm : Button = mView.findViewById(R.id.btConfirmCctv)
        var exText : TextView = mView.findViewById(R.id.textSelected)

        val mAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, Datas.instance.arrForListView)
        list.adapter = mAdapter
        list.setOnItemClickListener { _,_,i,_ ->
            exText.text = Datas.instance.arrForListView[i] + " 가 선택됨"
            Datas.instance.analIndex = i
            Datas.instance.analName = Datas.instance.arrForListView[i]
        }


        //  완료 버튼 클릭 시
        confirm.setOnClickListener {
            textView.text = "CCTV : "+Datas.instance.analName
            dialog.dismiss()
            dialog.cancel()
        }
        dialog.setView(mView)
        dialog.create()
        dialog.show()
    }
}