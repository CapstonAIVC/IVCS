package com.example.ivcs_android.view.streaming

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Switch
import android.widget.Toast
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.viewModel.Msocket
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

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
            Datas.instance.countSwitchSubject.onNext( (it as Switch).isChecked )
        }
        mBinding.densitySwitch.setOnClickListener {
            Datas.instance.densitySwitchSubject.onNext( (it as Switch).isChecked )
        }
    }
}