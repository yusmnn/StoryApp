package com.example.storyapp.data.remote.response

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

data class ResponseLocationStory(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    var listStory: List<ListStoryDetail>
)

data class ResponsePagingStory(
    @field:SerializedName("error")
    var error: String,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("listStory")
    var listStory: List<ListStoryDetail>
)

@Parcelize
@Entity(tableName = "stories")
data class ListStoryDetail(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,

    @ColumnInfo(name = "lat")
    val lat: Double? = null,

    @ColumnInfo(name = "lon")
    val lon: Double? = null

) : Parcelable