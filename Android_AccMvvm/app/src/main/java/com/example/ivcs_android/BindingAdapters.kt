package com.example.ivcs_android

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object BindingAdapters{
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