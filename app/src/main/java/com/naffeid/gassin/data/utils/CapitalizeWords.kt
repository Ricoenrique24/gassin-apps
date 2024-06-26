package com.naffeid.gassin.data.utils

object CapitalizeWords {
    fun String.capitalizeWords(): String {
        return this.split(" ")
            .joinToString(" ") {
                it.capitalize()
            }
            .trimEnd()
    }
}