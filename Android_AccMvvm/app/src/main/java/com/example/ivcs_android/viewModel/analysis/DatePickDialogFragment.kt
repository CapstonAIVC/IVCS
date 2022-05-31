package com.example.ivcs_android.viewModel.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.example.ivcs_android.databinding.DatePickBinding
import com.example.ivcs_android.model.DataAnal

class DatePickDialogFragment(dataAnal: DataAnal) : DialogFragment() {

    var dataAnal = dataAnal
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
            dataAnal.startTimeInfo = tmpTimeInfo
            dataAnal.startText.value =
                (dataAnal.startTimeInfo[0]).toString() + "년 " + (dataAnal.startTimeInfo[1]).toString() + "월 " + (dataAnal.startTimeInfo[2]).toString() + "일 " + (dataAnal.startTimeInfo[3]).toString() + "시"
        } else {
            dataAnal.endTimeInfo = tmpTimeInfo
            dataAnal.endText.value =
                (dataAnal.endTimeInfo[0]).toString() + "년 " + (dataAnal.endTimeInfo[1]).toString() + "월 " + (dataAnal.endTimeInfo[2]).toString() + "일 " + (dataAnal.endTimeInfo[3]).toString() + "시"
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
            binding.yearPicker.value = dataAnal.startTimeInfo[0].toInt()
            binding.monthPicker.value = dataAnal.startTimeInfo[1].toInt()
            binding.datePicker.value = dataAnal.startTimeInfo[2].toInt()
            binding.hourPicker.value = dataAnal.startTimeInfo[3].toInt()
        } else {
            binding.yearPicker.value = dataAnal.endTimeInfo[0].toInt()
            binding.monthPicker.value = dataAnal.endTimeInfo[1].toInt()
            binding.datePicker.value = dataAnal.endTimeInfo[2].toInt()
            binding.hourPicker.value = dataAnal.endTimeInfo[3].toInt()
        }
    }

}