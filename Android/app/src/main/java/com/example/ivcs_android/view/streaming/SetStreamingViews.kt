package com.example.ivcs_android.view.streaming

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Switch
import android.widget.Toast
import com.example.ivcs_android.databinding.ActivityStreamingBinding
import com.example.ivcs_android.model.Consts
import com.example.ivcs_android.model.Datas
import com.example.ivcs_android.viewModel.Msocket
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SetStreamingViews(context: Context, mBinding: ActivityStreamingBinding) {

    val context = context
    val mBinding = mBinding
    var arrForListView : Array<String> = arrayOf(Consts.tmpname)

    fun setViews(){
        setListView()
        setSwitchs()
    }

    fun setListView(){
        setNames()
        mBinding.cctvList.setOnItemClickListener { adapterView, view, i, l ->
            if(Msocket.instance.mSocket.connected()) {
                Msocket.instance.mSocket.emit("hls_req_test",arrForListView[i])
            }
            else{
                Msocket.instance.setSocket()
                Toast.makeText(context,"서버와 재연결 시도",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setNames(){
        val url = Consts.localhost+Consts.getUrl
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                Log.e("getNamesFailed",e.message.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                var jsonArr = JSONObject(response.body!!.string()).getJSONArray("name")
                arrForListView = Array(jsonArr.length()) {""}
                for( i in 0 until jsonArr.length()){
                    arrForListView[i] = jsonArr.getString(i)
                }
                var mAdapter = ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,arrForListView)
                mBinding.cctvList.adapter = mAdapter
            }
        })
    }

    fun setSwitchs(){
        mBinding.countingSwitch.setOnClickListener {
            Datas.instance.countSwitchSubject.onNext( (it as Switch).isChecked )
        }
        mBinding.densitySwitch.setOnClickListener {
            Datas.instance.densitySwitchSubject.onNext( (it as Switch).isChecked )
        }
    }

    fun setBt(){
//        mBinding.btSend.setOnClickListener {
//            if(Msocket.instance.mSocket.connected()) {
////                Msocket.instance.mSocket.emit("test", "It is sample")
//                Msocket.instance.mSocket.emit("hls_req_test","[수도권제1순환선] 성남")
//            }
//            else{
//                Msocket.instance.setSocket()
//                Toast.makeText(context,"서버와 재연결 시도",Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        //for test
//        mBinding.test.setOnClickListener {
////            var bmp = mBinding.exoPlayerView.drawToBitmap(Bitmap.Config.ARGB_8888)
//            var bmp : Bitmap = mBinding.textureView.bitmap!!
//            bmp = Bitmap.createScaledBitmap(bmp,160,120,true)
//
//            var intArray : IntArray = IntArray(bmp.width * bmp.height)
//            bmp.getPixels(intArray,0,bmp.width,0,0,bmp.width,bmp.height)
//            Log.e("testBt",intArray.size.toString())
//
////            Msocket.instance.mSocket.emit("start",intArray.contentToString())
//            Observable.just(bmp)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe (
//                    { mBinding.imageTest.setImageBitmap(bmp) },
//                    { Log.e("testBtErr",it.message.toString()) },
//                )
//        }
    }
}