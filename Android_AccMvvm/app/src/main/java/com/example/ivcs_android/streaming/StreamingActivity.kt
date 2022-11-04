package com.example.ivcs_android.streaming

import android.R
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.Statics
import com.google.android.exoplayer2.ExoPlayer

class StreamingActivity : AppCompatActivity() {

    lateinit var mBinding : ActivityStreamingBinding
    lateinit var streamingViewModel: StreamingViewModel
    lateinit var myPlayer: MyPlayer

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var fac = StreamingViewModel.StreamingFactory(StreamingModel().apply { myHeight.value = resources.displayMetrics.heightPixels/4 })
        streamingViewModel = ViewModelProvider(this,fac).get(StreamingViewModel::class.java)
        mBinding = ActivityStreamingBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@StreamingActivity
            viewModel = streamingViewModel
        }

        setAdapter()
        setPlayer()
        // Activity가 사라질 때 강제로 socket을 멈췄기 때문에 실행중이었던 판정이면 강제로 다시 실행시킨다.
        if(streamingViewModel.model.textCountShow.value!!) streamingViewModel.model.startSocket()
        setContentView(mBinding.root)
    }

    fun setPlayer(){
        myPlayer = MyPlayer(ExoPlayer.Builder(this).build())
        myPlayer.initializePlayer(mBinding)
    }


    private fun setAdapter(){
        var mAdapter = ArrayAdapter<String>(this, R.layout.simple_list_item_1, Statics.arrForListView)
        mBinding.cctvList.adapter = mAdapter
        mBinding.cctvList.setOnItemClickListener { _, _, i, _ ->
            streamingViewModel.model.cctvIdx = i
            myPlayer.setPlayerURL(Statics.arrForUrl[i])
        }
    }

    override fun onStop() {
        streamingViewModel.model.mSocket.releaseSocket()
        myPlayer.releasePlayer()
        super.onStop()
    }

}