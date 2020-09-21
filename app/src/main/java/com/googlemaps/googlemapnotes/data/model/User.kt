package com.googlemaps.googlemapnotes.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(

    @Expose
    @SerializedName("userEmail")
    val email: String,

    @Expose
    @SerializedName("userName")
    val userName: String
)