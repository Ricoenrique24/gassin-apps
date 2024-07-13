package com.naffeid.gassin.data.utils

object TransactionStatus {
    fun convertStatusToDescription(statusCode: String): String {
        return when (statusCode) {
            "1" -> "Menunggu Konfirmasi"
            "2" -> "Sedang Perjalanan"
            "3" -> "Selesai"
            "4" -> "Dibatalkan"
            else -> "Unknown Status"
        }
    }
}
