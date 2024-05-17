package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddStoryViewModel: ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun upload(photo: MultipartBody.Part, requestBody: RequestBody, token: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().uploadImg(photo, requestBody,"Bearer $token")
        api.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                _loading.value = false
                val responseBody = response.body()

                if (responseBody!!.error){
                    _message.value = responseBody.message
                } else {
                    _message.value = response.message()
                }
            }
            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _loading.value = false
                _message.value = "Pesan error :" + t.message.toString()
            }
        })
    }
}