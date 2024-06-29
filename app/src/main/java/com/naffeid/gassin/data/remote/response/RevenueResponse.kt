package com.naffeid.gassin.data.remote.response

import com.google.gson.annotations.SerializedName

data class RevenueResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("dailyRevenue")
	val dailyRevenue: String? = null
)
