package com.naffeid.gassin.data.remote.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StatusTransactionResponse(

	@field:SerializedName("statusTransaction")
	val statusTransaction: StatusTransaction? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class StatusTransaction(

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(updatedAt)
		parcel.writeString(createdAt)
		parcel.writeValue(id)
		parcel.writeString(status)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<StatusTransaction> {
		override fun createFromParcel(parcel: Parcel): StatusTransaction {
			return StatusTransaction(parcel)
		}

		override fun newArray(size: Int): Array<StatusTransaction?> {
			return arrayOfNulls(size)
		}
	}
}
