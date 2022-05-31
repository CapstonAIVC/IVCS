package com.example.ivcs_android

import android.R
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.view.streaming.SetStreamingViews
import com.example.ivcs_android.view.streaming.Streaming
import com.example.ivcs_android.Presenter.Streaming.StreamingBind
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

class StreamingActivity : AppCompatActivity() {

    lateinit var mBinding : ActivityStreamingBinding
    lateinit var setStreamingViews : SetStreamingViews
    lateinit var mStreaming: Streaming
    lateinit var streamingBind: StreamingBind

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStreamingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.textureView.layoutParams.height = resources.displayMetrics.heightPixels/4
        mBinding.cctvList.layoutParams.height = resources.displayMetrics.heightPixels/4
        setAdapter()
        initStreamingActivity()
    }

    private fun setAdapter(){
        // context때문에 Activity에서 처리
        var mAdapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, Datas.instance.arrForListView)
        Observable.just(mAdapter)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {mBinding.cctvList.adapter = it}
    }

    private fun initStreamingActivity(){
        mStreaming = Streaming(this,mBinding)
        mStreaming.initializePlayer()

        streamingBind = StreamingBind(mStreaming, mBinding)
        streamingBind.initStreamingBind()

        setStreamingViews = SetStreamingViews(this, mBinding, streamingBind.eventSubject)
        setStreamingViews.setViews()
    }

    override fun onDestroy() {
        streamingBind.mSocket.releaseSocket()
        streamingBind.countSwitchSubject.onNext(false)
        mStreaming.releasePlayer()
        super.onDestroy()
    }

}