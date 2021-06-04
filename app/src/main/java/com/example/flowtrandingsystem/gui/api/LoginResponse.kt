package com.example.flowtrandingsystem.gui.api

import com.example.flowtrandingsystem.gui.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse (

    @SerializedName("token")
    var token: String,

    @SerializedName("user")
    var user: User
)