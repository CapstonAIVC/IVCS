package com.example.ivcs_android.view.streaming

import android.content.Context
import android.util.Log
import android.widget.Switch
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.viewModel.Streaming.Msocket
import io.reactivex.rxjava3.subjects.PublishSubject

class SetStreamingViews(context: Context, mBinding: ActivityStreamingBinding, eventSubject : PublishSubject<Int>) {

    val context = context
    val mBinding = mBinding
    var eventSubject = eventSubject

    fun setViews(){
        setListView()
        setSwitchs()
    }

    fun setListView(){
        mBinding.cctvList.setOnItemClickListener { _, _, i, _ ->
            eventSubject.onNext(i)
        }
    }

    fun setSwitchs(){
        mBinding.countingSwitch.setOnClickListener {
            eventSubject.onNext(Consts.switchCounting)
        }
        mBinding.densitySwitch.setOnClickListener {
            eventSubject.onNext(Consts.switchDensity)
        }
    }
}