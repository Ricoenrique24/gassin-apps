package com.naffeid.gassin.data.model

data class ResupplyTransaction(
    val id: Int,
    val idStore: Int,
    val idUser: Int,
    val qty: Int,
    val totalPayment: Int,
    val status: Int,
    val note: String,
    val timeStamp: String,
)
