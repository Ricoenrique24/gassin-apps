package com.naffeid.gassin.data.model

data class PurchaseTransaction(
    val id: Int,
    val idCustomer: Int,
    val idUser: Int,
    val qty: Int,
    val totalPayment: Int,
    val status: Int,
    val note: String,
    val timeStamp: String,
)
