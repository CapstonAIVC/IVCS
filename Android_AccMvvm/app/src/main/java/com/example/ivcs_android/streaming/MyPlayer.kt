package com.example.ivcs_android.streaming

import android.content.Context
import android.util.Log
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class MyPlayer(var player: ExoPlayer) {

    fun initializePlayer(mBinding : ActivityStreamingBinding) {
        releasePlayer()
        // exoplayerView의 player 생성 및 지정
        player!!.setVideoTextureView( mBinding.textureView )
        catchPlayerErr()
    }

    fun setPlayerURL(url : String){
        player!!.stop()
        try {
            player!!.setMediaSource( getMediaSource(url) )
            player!!.prepare()
            player!!.playWhenReady = true
        }catch( e : Exception ) {
        }
    }

    fun catchPlayerErr(){
        player!!.addListener( object : Player.Listener{
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Log.e("player에러",error.message.toString())
            }
        } )
    }

    private fun getMediaSource(urlStr : String) : MediaSource{

        var mediaItem = MediaItem.fromUri(urlStr)
        var mHttpDataSFac = DefaultHttpDataSource.Factory()
        var mediaSource = HlsMediaSource.Factory(mHttpDataSFac).createMediaSource(mediaItem)

        return mediaSource
    }
    fun releasePlayer(){
        player.release()
    }
}