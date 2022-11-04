package com.example.ivcs_android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.example.ivcs_android.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    // 수정을 위해 임시로 있는 context
    companion object{
        lateinit var appContext : Context
        // xml에 바인딩 하기 위함
        @BindingAdapter("bindImageBitmap")
        @JvmStatic
        fun loadImage(iv : ImageView, bitmap : Bitmap?) {
            // bitmap이 null이 아닐때만 실행됨
            bitmap?.let {
                iv.setImageBitmap(it)
            }
        }
        // xml에 바인딩 하기 위함
        @BindingAdapter("setLayoutHeight")
        @JvmStatic
        fun setLayoutSize(v : View, height: Int?) {
            // bitmap이 null이 아닐때만 실행됨
            v.layoutParams = v.layoutParams.also { params ->
                height?.let { params.height = height }
            }
        }
    }

    lateinit var startBinding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 수정인 위한 임시 context
        appContext = applicationContext

        startBinding =
            DataBindingUtil.setContentView<ActivityStartBinding>(this, R.layout.activity_start)
        startBinding.lifecycleOwner = this // 라이브데이터 관찰을 위함
        startBinding.viewModel = StartViewModel(application)

        checkPermissions()
    }


    //퍼미션 체크 및 권한 요청 함수
    private fun checkPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE)
        //거절되었거나 아직 수락하지 않은 권한(퍼미션)을 저장할 문자열 배열 리스트
        var rejectedPermissionList = ArrayList<String>()

        //필요한 퍼미션들을 하나씩 끄집어내서 현재 권한을 받았는지 체크
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED
            ) {
                //만약 권한이 없다면 rejectedPermissionList에 추가
                rejectedPermissionList.add(permission)
            }
        }
        //거절된 퍼미션이 있다면...
        if (rejectedPermissionList.isNotEmpty()) {
            //권한 요청!
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), 100)

            checkPermissions()
        }
        else{
            startBinding.viewModel?.let{ it.setInfo()}
        }
    }
}