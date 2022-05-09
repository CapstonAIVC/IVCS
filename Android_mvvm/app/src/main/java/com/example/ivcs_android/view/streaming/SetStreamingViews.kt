package com.example.ivcs_android.view.streaming

import android.content.Context
import android.widget.Switch
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.viewModel.Streaming.Msocket

class SetStreamingViews(context: Context, mBinding: ActivityStreamingBinding) {

    val context = context
    val mBinding = mBinding

    fun setViews(){
        setListView()
        setSwitchs()
    }

    fun setListView(){
        mBinding.cctvList.setOnItemClickListener { _, _, i, _ ->
//            Datas.instance.cctvName = Datas.instance.arrForListView[i]
            Datas.instance.cctvIdx = i
            Datas.instance.changeUrlSubject.onNext(Datas.instance.arrForUrl[i])
        }
    }

    fun setSwitchs(){
        mBinding.countingSwitch.setOnClickListener {
            if( !Msocket.instance.checkSocket( context )){
                ( it as Switch ).isChecked = false
            }
            else {
                Datas.instance.countSwitchSubject.onNext((it as Switch).isChecked)
            }
        }
        mBinding.densitySwitch.setOnClickListener {
            Datas.instance.densitySwitchSubject.onNext( (it as Switch).isChecked )
        }
    }
}