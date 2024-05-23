package com.example.storyapp.data.remote

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class KeyRemote(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val  nextKey: Int?

)
