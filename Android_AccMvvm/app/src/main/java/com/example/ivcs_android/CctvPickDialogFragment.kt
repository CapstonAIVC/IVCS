package com.example.ivcs_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.example.ivcs_android.databinding.CctvPickBinding
import com.example.ivcs_android.model.DataAnal
import com.example.ivcs_android.model.Datas

class CctvPickDialogFragment( dataAnal : DataAnal ) : DialogFragment() {

    private lateinit var binding: CctvPickBinding
    var dataAnal = dataAnal
    var selectedText : MutableLiveData<String> = MutableLiveData("선택해 주세요. ")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = CctvPickBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.frag = this

        val mAdapter = ArrayAdapter<String>(requireContext(),
            android.R.layout.simple_list_item_1,
            Datas.instance.arrForListView)
        binding.listViewCctvName.adapter = mAdapter
        binding.listViewCctvName.setOnItemClickListener { _, _, i, _ ->
            selectedText.value = Datas.instance.arrForListView[i] + " 가 선택됨"
            dataAnal.analIndex = i
        }

        return binding.root
    }

    fun clickConfirm( view : View ){
        dataAnal.analName = Datas.instance.arrForListView[dataAnal.analIndex]
        dataAnal.cctvText.value = "CCTV : " + dataAnal.analName
        dismiss()
    }


}