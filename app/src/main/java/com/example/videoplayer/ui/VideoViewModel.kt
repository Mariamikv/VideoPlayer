package com.example.videoplayer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoViewModel: ViewModel() {

    private val progressLiveData: MutableLiveData<*> = MutableLiveData<Any>()

    fun getProgress(): LiveData<*> {
        return progressLiveData
    }

}