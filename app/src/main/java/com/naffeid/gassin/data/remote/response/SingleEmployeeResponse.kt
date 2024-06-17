package com.naffeid.gassin.data.remote.response

import com.google.gson.annotations.SerializedName

data class SingleEmployeeResponse(

	@field:SerializedName("employee")
	val employee: Employee? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Employee(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("apikey")
	val apikey: Any? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("token_fcm")
	val tokenFcm: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
