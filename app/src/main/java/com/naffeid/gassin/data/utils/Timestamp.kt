package com.naffeid.gassin.data.utils

import java.text.SimpleDateFormat
import java.util.Locale

object Timestamp {
    // Formatter untuk parsing ISO 8601 format
    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())

    // Formatter untuk output yang diinginkan
    private val desiredFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    fun convertToDesiredFormat(dateTimeString: String): String {
        // Parsing string ISO 8601 ke Date
        val date = isoFormatter.parse(dateTimeString)

        // Format Date ke format yang diinginkan
        return desiredFormatter.format(date)
    }
}