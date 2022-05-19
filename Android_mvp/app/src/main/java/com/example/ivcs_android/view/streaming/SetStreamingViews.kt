package com.example.ivcs_android.view.streaming

import android.content.Context
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Consts
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