package com.naffeid.gassin.data.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object Timestamp {
    private val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    private val desiredFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    init {
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC") // Set time zone to UTC for ISO format
    }

    fun convertToDesiredFormat(dateTimeString: String): String {
        val date = isoFormatter.parse(dateTimeString) // Parse ISO formatted date as UTC

        // Format date in desired local time zone
        desiredFormatter.timeZone = TimeZone.getDefault() // Set time zone to default local time zone
        return desiredFormatter.format(date)
    }
}
