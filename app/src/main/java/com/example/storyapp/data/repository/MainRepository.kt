package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.database.StoryDatabase
import com.example.storyapp.data.remote.response.DataLogin
import com.example.storyapp.data.remote.response.DataRegister
import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.response.ListStoryDetail
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.ResponseLocationStory
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.wrapEspressoIdlingResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainRepository (private val database: StoryDatabase, private val apiService: ApiService) {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _userLogin = MutableLiveData<LoginResponse>()
    val userLogin: LiveData<LoginResponse> = _userLogin

    private var _stories = MutableLiveData<List<ListStoryDetail>>()
    var stories: LiveData<List<ListStoryDetail>> = _stories

    fun getLoginResponse(dataLogin: DataLogin) {
        wrapEspressoIdlingResource {
            _loading.value = true
            val api = ApiConfig.getApiService().userLogin(dataLogin)
            api.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    _loading.value = false
                    val responseBody = response.body()

                    if (response.isSuccessful) {
                        _userLogin.value = responseBody!!
                        _message.value = "Hi ${_userLogin.value!!.loginResult.name}!"
                    } else {
                        when (response.code()) {
                            401 -> _message.value = "Email atau password yang anda masukan salah, silahkan coba lagi"
                            408 -> _message.value = "Koneksi internet anda lambat, silahkan coba lagi"
                            else -> _message.value = "Pesan error: " + response.message()
                        }
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _loading.value = false
                    _message.value = "Pesan error: " + t.message.toString()
                }
            })
        }
    }

    fun getRegisterResponse(dataRegister: DataRegister) {
        wrapEspressoIdlingResource {
            _loading.value = true
            val api = ApiConfig.getApiService().userRegis(dataRegister)
            api.enqueue(object : Callback<DetailResponse> {
                override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                    _loading.value = false
                    if (response.isSuccessful){
                        _message.value = "Berhasil membuat akun"
                    } else {
                        when (response.code()) {
                            401 -> _message.value = "Error aja"
                            408 -> _message.value = "Tidak terhubung internet, silakan coba lagi"
                            else -> _message.value = "Pesan error :" + response.message()
                        }
                    }
                }

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                    _loading.value = false
                    _message.value = "Pesan error :" + t.message.toString()
                }
            })
        }
    }

    fun upload(photo: MultipartBody.Part, des: RequestBody, lat: Double?, lng: Double?, token: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().uploadStory(photo, des, lat?.toFloat(), lng?.toFloat(), "Bearer $token")
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

    fun getStory(token: String) {
        _loading.value = true
        val api = ApiConfig.getApiService().getStoryLocation(32, 1,"Bearer $token")
        api.enqueue(object : Callback<ResponseLocationStory> {
            override fun onResponse(call: Call<ResponseLocationStory>, response: Response<ResponseLocationStory>) {
                _loading.value = false

                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null){
                        _stories.value = responseBody.listStory
                    }
                    _message.value = responseBody?.message.toString()
                } else {
                    _message.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseLocationStory>, t: Throwable) {
                _loading.value = false
                _message.value = "Pesan error :" + t.message.toString()
            }
        })
    }

    @ExperimentalPagingApi
    fun getPagingStories(token: String): LiveData<PagingData<ListStoryDetail>> {
        val pager = Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryMediator(database, apiService, token),
            pagingSourceFactory = {
                database.getListStoryDetailDao().getStories()
            }
        )
        return pager.liveData
    }
}