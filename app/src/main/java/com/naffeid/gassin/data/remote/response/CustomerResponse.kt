package com.naffeid.gassin.data.remote.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CustomerResponse(

	@field:SerializedName("listCustomer")
	val listCustomer: List<ListCustomerItem> = emptyList(),

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListCustomerItem(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("link_map")
	val linkMap: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(address)
		parcel.writeString(updatedAt)
		parcel.writeString(phone)
		parcel.writeString(linkMap)
		parcel.writeString(price)
		parcel.writeString(name)
		parcel.writeString(createdAt)
		parcel.writeValue(id)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ListCustomerItem> {
		override fun createFromParcel(parcel: Parcel): ListCustomerItem {
			return ListCustomerItem(parcel)
		}

		override fun newArray(size: Int): Array<ListCustomerItem?> {
			return arrayOfNulls(size)
		}
	}
}
