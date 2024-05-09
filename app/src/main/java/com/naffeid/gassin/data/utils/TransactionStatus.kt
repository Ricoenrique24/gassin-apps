package com.naffeid.gassin.data.utils

class TransactionStatus(val statusCode:Int) {
    companion object {
        const val STATUS_IN = 0
        const val STATUS_OUT = 1
        const val STATUS_ENTRY_PROCESS = 2
        const val STATUS_EXIT_PROCESS = 3
        const val STATUS_CANCELLED_EXIT = 4
        const val STATUS_CANCELLED_ENTRY = 5
        const val STATUS_UNKNOWN = -1
    }

    fun getStatusDescription(): String {
        return when (statusCode) {
            STATUS_IN -> "Masuk"
            STATUS_OUT -> "Keluar"
            STATUS_ENTRY_PROCESS -> "Proses Masuk"
            STATUS_EXIT_PROCESS -> "Proses Keluar"
            STATUS_CANCELLED_ENTRY -> "Batal Masuk"
            STATUS_CANCELLED_EXIT -> "Batal Keluar"
            else -> "Status transaksi tidak diketahui"
        }
    }

}