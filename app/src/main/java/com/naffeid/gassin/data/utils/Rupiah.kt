package com.naffeid.gassin.data.utils

import java.text.NumberFormat
import java.util.Locale

object Rupiah {
    fun convertToRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        var formattedAmount = numberFormat.format(amount)

        if (formattedAmount.endsWith(",00")) {
            formattedAmount = formattedAmount.substring(0, formattedAmount.length - 3)
        }

        if (formattedAmount.startsWith("Rp")) {
            formattedAmount = formattedAmount.replace("Rp", "Rp ")
        }

        return formattedAmount
    }
}