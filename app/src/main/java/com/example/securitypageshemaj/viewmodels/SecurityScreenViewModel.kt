package com.example.securitypageshemaj.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SecurityScreenViewModel : ViewModel() {

    private val _password = MutableLiveData<List<Int>>().apply {
        mutableListOf<Int>()
    }

    val password: LiveData<List<Int>> = _password

    fun setPassword(finalPass: List<Int>) {
        _password.postValue(finalPass)
    }

}