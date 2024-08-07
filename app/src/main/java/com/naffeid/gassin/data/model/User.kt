package com.naffeid.gassin.data.model

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val role: String,
    val apikey: String,
    val tokenfcm: String,
)