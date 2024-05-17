package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.database.SsPreferences
import kotlinx.coroutines.launch

class UserViewModel(private val preferences: SsPreferences) : ViewModel() {
    fun getLogin(): LiveData<Boolean> {
        return preferences.getLoginSession().asLiveData()
    }

    fun saveLogin(loginSes: Boolean){
        viewModelScope.launch {
            preferences.saveLoginSession(loginSes)
        }
    }

    fun saveName(name: String) {
        viewModelScope.launch {
            preferences.saveName(name)
        }
    }

    fun getToken(): LiveData<String> {
        return preferences.getToken().asLiveData()
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            preferences.saveToken(token)
        }
    }

    fun clearLogin() {
        viewModelScope.launch {
            preferences.clearLogin()
        }
    }
}