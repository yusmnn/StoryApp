package com.example.storyapp.data.remote.retrofit

import com.example.storyapp.data.remote.response.DataLogin
import com.example.storyapp.data.remote.response.DataRegister
import com.example.storyapp.data.remote.response.DetailResponse
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.ResponseLocationStory
import com.example.storyapp.data.remote.response.ResponsePagingStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {
    @POST("login")
    fun userLogin(@Body requestLogin: DataLogin): Call<LoginResponse>

    @POST("register")
    fun userRegis(@Body requestRegister: DataRegister): Call<DetailResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Part file:MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<DetailResponse>

    @GET("stories")
    fun getStoryLocation(
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): Call<ResponseLocationStory>

    @GET("stories")
    suspend fun getPaging(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): ResponsePagingStory
}
