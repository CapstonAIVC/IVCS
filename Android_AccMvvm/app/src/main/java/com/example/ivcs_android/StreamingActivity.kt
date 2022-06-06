package com.example.ivcs_android

import android.R
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.viewModel.streaming.StreamingViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

class StreamingActivity : AppCompatActivity() {

    lateinit var mBinding : ActivityStreamingBinding
    lateinit var streamingViewModel: StreamingViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStreamingBinding.inflate(layoutInflater)
        streamingViewModel = StreamingViewModel(application)
        mBinding.viewModel = streamingViewModel
        mBinding.lifecycleOwner = this
        setContentView(mBinding.root)

//        mBinding.textureView.layoutParams.height = resources.displayMetrics.heightPixels/4
        mBinding.streamingFrameLayout.layoutParams.height = resources.displayMetrics.heightPixels/4
        mBinding.cctvList.layoutParams.height = resources.displayMetrics.heightPixels/4

        setAdapter()
        streamingViewModel.myPlayer.initializePlayer(mBinding)
    }

    private fun setAdapter(){
        var mAdapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, Datas.instance.arrForListView)
        mBinding.cctvList.adapter = mAdapter
        mBinding.cctvList.setOnItemClickListener { _, _, i, _ ->
            streamingViewModel.dataStreaming.cctvIdx = i
            streamingViewModel.dataStreaming.changeUrlSubject.onNext(Datas.instance.arrForUrl[i])
        }
    }

    override fun onDestroy() {
        streamingViewModel.mSocket.releaseSocket()
        streamingViewModel.myPlayer.releasePlayer()
        streamingViewModel.dataStreaming.countSwitchSubject.onNext(false)
        super.onDestroy()
    }

}