package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.response.DataLogin
import com.example.storyapp.data.remote.response.DataRegister
import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Response


class LoginViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _userLogin = MutableLiveData<LoginResponse>()
    val userLogin: LiveData<LoginResponse> = _userLogin

    var isError: Boolean = false

    fun getLoginResponse(dataLogin: DataLogin) {
        _loading.value = true
        val api = ApiConfig.getApiService().userLogin(dataLogin)
        api.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _loading.value = false
                val responseBody = response.body()

                if (response.isSuccessful) {
                    isError = false
                    _userLogin.value = responseBody!!
                    _message.value = "${_userLogin.value!!.loginResult?.name} berhasil login"
                } else {
                    isError = true
                    when (response.code()) {
                        401 -> _message.value = "Email atau password yang anda masukan salah, silahkan coba lagi"
                        408 -> _message.value = "Koneksi internet anda lambat, silahkan coba lagi"
                        else -> _message.value = "Pesan error: " + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isError = true
                _loading.value = false
                _message.value = "Pesan error: " + t.message.toString()
            }
        })
    }

    fun getRegisterResponse(dataRegister: DataRegister) {
        _loading.value = true
        val api = ApiConfig.getApiService().userRegis(dataRegister)
        api.enqueue(object : retrofit2.Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                _loading.value = false
                if (response.isSuccessful){
                    isError = false
                    _message.value = "Berhasil membuat akun"
                } else {
                    isError = true
                    when (response.code()) {
                        401 -> _message.value = "Error aja"
                        408 -> _message.value = "Tidak terhubung internet, silakan coba lagi"
                        else -> _message.value = "Pesan error :" + response.message()
                    }
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                isError = true
                _loading.value = false
                _message.value = "Pesan error :" + t.message.toString()
            }
        })
    }
}