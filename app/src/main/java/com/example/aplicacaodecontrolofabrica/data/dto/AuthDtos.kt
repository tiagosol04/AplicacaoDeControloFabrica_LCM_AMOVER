package com.example.aplicacaodecontrolofabrica.data.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("usernameOrEmail")
    val usernameOrEmail: String,

    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName(value = "token", alternate = ["Token"])
    val token: String,

    @SerializedName(value = "userId", alternate = ["UserId"])
    val userId: String,

    @SerializedName(value = "username", alternate = ["Username"])
    val username: String,

    @SerializedName(value = "email", alternate = ["Email"])
    val email: String? = null,

    @SerializedName(value = "roles", alternate = ["Roles"])
    val roles: List<String> = emptyList(),

    @SerializedName(value = "expiresInMinutes", alternate = ["ExpiresInMinutes"])
    val expiresInMinutes: Int? = null
)

data class MeResponse(
    @SerializedName(value = "userId", alternate = ["UserId"])
    val userId: String,

    @SerializedName(value = "username", alternate = ["Username"])
    val username: String,

    @SerializedName(value = "email", alternate = ["Email"])
    val email: String? = null,

    @SerializedName(value = "roles", alternate = ["Roles"])
    val roles: List<String> = emptyList()
)