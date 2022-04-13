package com.example.ivcs_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ivcs_android.databinding.ActivityAnalysisBinding

class AnalysisActivity : AppCompatActivity() {

    lateinit var analisysBinding : ActivityAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analisysBinding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(analisysBinding.root)
    }
}