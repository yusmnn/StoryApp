package com.example.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.response.ResponseStory
import com.example.storyapp.data.remote.response.StoryDetailResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    var story: List<StoryDetailResponse> = listOf()
    var isError: Boolean = false

    fun getStory(token: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().getStories("Bearer $token")
        api.enqueue(object : Callback<ResponseStory> {
            override fun onResponse(call: Call<ResponseStory>, response: Response<ResponseStory>) {
                _loading.value = false

                if (response.isSuccessful){
                    isError = false
                    val responseBody = response.body()
                    if (responseBody != null){
                        story = responseBody.listStory
                    }
                    _message.value = responseBody?.message.toString()
                } else {
                    isError = true
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseStory>, t: Throwable) {
                _loading.value = false
                isError = true
                _message.value = "Pesan error :" + t.message.toString()
            }
        })
    }
}