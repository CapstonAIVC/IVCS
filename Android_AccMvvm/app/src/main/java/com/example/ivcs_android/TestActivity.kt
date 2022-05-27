package com.example.ivcs_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import com.example.ivcs_android.databinding.ActivityTestBinding

class TestActivity : AppCompatActivity() {
    lateinit var mWebView : WebView
    lateinit var testBinding : ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testBinding = DataBindingUtil.setContentView(this, R.layout.activity_analysis)

        mWebView = testBinding.testWebView
        

    }
}