package com.example.ivcs_android.viewModel

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.ivcs_android.AnalysisActivity
import com.example.ivcs_android.StartActivity
import com.example.ivcs_android.StreamingActivity
import com.example.ivcs_android.model.Datas

class StartViewModel : ViewModel() {

    fun btGoToStreamingClicked(){
        if(checkUrlInfo()) {
            val intent = Intent(StartActivity.appContext, StreamingActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            StartActivity.appContext.startActivity(intent)
        }
    }

    fun btGoToAnalysisClicked(){
        if(checkUrlInfo()){
            val intent = Intent(StartActivity.appContext, AnalysisActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            StartActivity.appContext.startActivity(intent)
        }
    }

    private fun checkUrlInfo(): Boolean {
        if (Datas.instance.arrForUrl.isEmpty()) {
            Datas.instance.setInfo()
            Toast.makeText(StartActivity.appContext, "서버 정보 요청중", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            Datas.instance.setInfo()
            return true
        }
    }

}