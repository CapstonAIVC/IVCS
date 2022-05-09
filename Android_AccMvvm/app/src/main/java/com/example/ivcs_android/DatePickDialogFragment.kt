package com.example.ivcs_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.ivcs_android.databinding.DatePickBinding
import com.example.ivcs_android.model.AnalysisEvent
import com.example.ivcs_android.model.Datas

class DatePickDialogFragment : DialogFragment() {

    private lateinit var binding: DatePickBinding
    private var isStart: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DatePickBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.frag = this
        isStart = requireArguments().getBoolean("isStart")

        setViews()

        return binding.root
    }

    fun confirmCilcked() {
        var tmpTimeInfo = arrayOf<Long>(
            binding.yearPicker.value.toLong(),
            binding.monthPicker.value.toLong(),
            binding.datePicker.value.toLong(),
            binding.hourPicker.value.toLong())

        if (isStart) { //여기아래 text바꾸는거 데이터바인딩으로 바꾸자
            Datas.instance.startTimeInfo = tmpTimeInfo
            Datas.instance.changeAnalInfoSubj.onNext(AnalysisEvent.ChangeStart)
        } else {
            Datas.instance.endTimeInfo = tmpTimeInfo
            Datas.instance.changeAnalInfoSubj.onNext(AnalysisEvent.ChangeEnd)
        }
        this.dismiss()
    }

    fun cancelClicked() {
        this.dismiss()
    }

    fun setViews() {
        //  순환 안되게 막기
        binding.yearPicker.wrapSelectorWheel = false

        //  editText 설정 해제
        binding.yearPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.monthPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.datePicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.hourPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        //  최소값 설정
        binding.yearPicker.minValue = 2019
        binding.monthPicker.minValue = 1
        binding.datePicker.minValue = 1
        binding.hourPicker.minValue = 0

        //  최대값 설정
        binding.yearPicker.maxValue = 2023
        binding.monthPicker.maxValue = 12
        binding.datePicker.maxValue = 31
        binding.hourPicker.maxValue = 23

        // 시작 날짜 설정
        if (isStart) {
            binding.yearPicker.value = Datas.instance.startTimeInfo[0].toInt()
            binding.monthPicker.value = Datas.instance.startTimeInfo[1].toInt()
            binding.datePicker.value = Datas.instance.startTimeInfo[2].toInt()
            binding.hourPicker.value = Datas.instance.startTimeInfo[3].toInt()
        } else {
            binding.yearPicker.value = Datas.instance.endTimeInfo[0].toInt()
            binding.monthPicker.value = Datas.instance.endTimeInfo[1].toInt()
            binding.datePicker.value = Datas.instance.endTimeInfo[2].toInt()
            binding.hourPicker.value = Datas.instance.endTimeInfo[3].toInt()
        }
    }

}