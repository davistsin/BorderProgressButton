package com.davistsin.progressbutton

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    val progress: MutableLiveData<Int> = MutableLiveData(0)
    var progressText: MutableLiveData<String> = MutableLiveData("0%")
    var inputValue: String = ""

    fun onEditTextChange(s: CharSequence){
        inputValue = s.toString()
    }

    fun setValueCLick() {
        if (inputValue.isEmpty()) return
        progress.value = inputValue.toInt()
        progressText.value = "$inputValue%"
    }
}