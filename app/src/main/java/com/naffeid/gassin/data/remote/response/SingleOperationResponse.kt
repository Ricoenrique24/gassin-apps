package com.naffeid.gassin.data.remote.response

import com.google.gson.annotations.SerializedName

data class SingleOperationResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("operation_transaction")
	val operationTransaction: OperationTransaction? = null
)
data class OperationTransaction(

	@field:SerializedName("note")
	val note: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("verified")
	val verified: Int? = null,

	@field:SerializedName("id_transaction")
	val idTransaction: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("resupply")
	val resupply: Resupply? = null,

	@field:SerializedName("purchase")
	val purchase: Purchase? = null,

	@field:SerializedName("total_payment")
	val totalPayment: String? = null,

	@field:SerializedName("id_category_transaction")
	val idCategoryTransaction: Int? = null,

	@field:SerializedName("category_transaction")
	val categoryTransaction: CategoryTransaction? = null,

	@field:SerializedName("user")
	val user: User? = null,
)
