package com.example.ivcs_android.view.streaming

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.ivcs_android.StartActivity
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Datas
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class Streaming(context: Context, mBinding: ActivityStreamingBinding) {
    private var player: ExoPlayer? = null
    var mContext : Context = context
    var mBinding : ActivityStreamingBinding = mBinding

    fun initializePlayer() {
        // exoplayerView의 player 생성 및 지정
        player = ExoPlayer.Builder(mContext)
            .build()
        player!!.setVideoTextureView( mBinding.textureView )

    }

    fun setPlayerURL(url : String){
        player!!.stop()
        try {
            player!!.setMediaSource( getMediaSource(url) )
            player!!.prepare()
            player!!.playWhenReady = true
        }catch( e : Exception ) {
            Toast.makeText(StartActivity.appContext,"5초후에 시도해주세요",Toast.LENGTH_SHORT).show()
            Datas.instance.setInfo()
        }
//        player!!.setMediaSource( getMediaSource(url) )
//        player!!.prepare()
//        player!!.playWhenReady = true
    }

    private fun getMediaSource(urlStr : String) : MediaSource{

        var mediaItem = MediaItem.fromUri(urlStr)
        var mHttpDataSFac = DefaultHttpDataSource.Factory()
        var mediaSource = HlsMediaSource.Factory(mHttpDataSFac).createMediaSource(mediaItem)

        return mediaSource
    }
    fun releasePlayer(){
        if(player != null){
//            playbackPosition = player.getCurrentPosition();
//            currentWindow = player.getCurrentWindowIndex();
//            playWhenReady = player.getPlayWhenReady();

//            mBinding.exoPlayerView.player = null
            player!!.release()
            player = null
        }
    }
}