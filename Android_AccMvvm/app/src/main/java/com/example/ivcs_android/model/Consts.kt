package com.example.ivcs_android.model

class Consts {
    companion object{
        //// url
        const val serverMainUrl = "http://119.207.210.53:7008"
        const val serverDataUrl = "http://119.207.210.53:7009"
        const val getUrl = "/getUrl_client"
        const val plotUrl = "/req_plot"
        const val localhost = "http://10.0.2.2:3000"
        const val localhost_DataServer = "http://10.0.2.2:4000"
        const val hlstest = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"

        // 시간, 날,월
        
        const val hour : String = "hour"
        const val day : String = "day"
        const val month : String = "month"

        // 분석화면 버튼들
        const val setStart = "setStart"
        const val setEnd = "setEnd"
        const val setCctv = "setCctv"
        const val requestAnal = "requestAnal"

        // 그래프 하단
        val bottomDay = arrayListOf( "0시", "1시", "2시", "3시", "4시", "5시", "6시", "7시", "8시", "9시", "10시", "11시", "12시", "13시", "14시", "15시", "16시", "17시", "18시", "19시", "20시", "21시", "22시", "23시" )
        val bottomMonth = arrayListOf("1일", "2일", "3일", "4일", "5일", "6일", "7일", "8일", "9일", "10일", "11일", "12일", "13일", "14일", "15일", "16일", "17일", "18일", "19일", "20일", "21일", "22일", "23일", "24일", "25일", "26일", "27일", "28일", "29일", "30일", "31일")
        val bottomYear = arrayListOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월","12월")
    }
}