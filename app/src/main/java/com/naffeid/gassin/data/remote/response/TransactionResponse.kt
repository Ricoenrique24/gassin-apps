package com.naffeid.gassin.data.remote.response

import com.google.gson.annotations.SerializedName

data class TransactionResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("listTransaction")
	val listTransaction: List<ListTransactionItem?>? = null
)

data class ListTransactionItem(

	@field:SerializedName("note")
	val note: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("status_transaction")
	val statusTransaction: StatusTransaction? = null,

	@field:SerializedName("id_user")
	val idUser: Int? = null,

	@field:SerializedName("store")
	val store: Store? = null,

	@field:SerializedName("id_store")
	val idStore: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("total_payment")
	val totalPayment: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("id_customer")
	val idCustomer: Int? = null,

	@field:SerializedName("customer")
	val customer: Customer? = null
)