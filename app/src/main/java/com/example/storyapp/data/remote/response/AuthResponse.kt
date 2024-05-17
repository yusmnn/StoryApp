package com.example.storyapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class DataLogin(
    var email: String,
    var password: String
)

data class LoginResponse(
    @field:SerializedName("loginResult")
    var loginResult: LoginResult,

    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("message")
    var message: String
)

data class LoginResult(
    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("userId")
    var userId: String,

    @field:SerializedName("token")
    var token: String
)

data class DataRegister(
    var name: String,
    var email: String,
    var password: String
)

data class DetailResponse(
    var error: Boolean,
    var message: String
)

data class ResponseStory(
    var error: String,
    var message: String,
    var listStory: List<StoryDetailResponse>
)

@Parcelize
data class StoryDetailResponse (
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
    var lat: Double,
    var lon: Double
) : Parcelable
