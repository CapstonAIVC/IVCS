package com.example.ivcs_android

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.Switch
import androidx.databinding.BindingAdapter

class BindAdapters {
    object BindAdapterImageView{
        // xml에 바인딩 하기 위함
        @BindingAdapter("bindImageBitmap")
        @JvmStatic
        fun loadImage(iv : ImageView, bitmap : Bitmap?) {
            // bitmap이 null이 아닐때만 실행됨
            bitmap?.let {
                iv.setImageBitmap(it)
            }
        }
    }
    object BindAdapterBoolSwitch{
        // xml에 바인딩 하기 위함
        @BindingAdapter("bindBoolSwitch")
        @JvmStatic
        fun setSwitch(switch : Switch, ischecked : Boolean) {
            // bitmap이 null이 아닐때만 실행됨
            switch.isChecked = ischecked
        }
    }
}