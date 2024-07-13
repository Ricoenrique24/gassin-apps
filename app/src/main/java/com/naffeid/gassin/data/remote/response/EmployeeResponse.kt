package com.naffeid.gassin.data.remote.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class EmployeeResponse(

	@field:SerializedName("listEmployee")
	val listEmployee: List<ListEmployeeItem> = emptyList(),

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListEmployeeItem(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("apikey")
	val apikey: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("token_fcm")
	val tokenFcm: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(role)
		parcel.writeString(apikey)
		parcel.writeString(updatedAt)
		parcel.writeString(phone)
		parcel.writeString(name)
		parcel.writeString(tokenFcm)
		parcel.writeString(createdAt)
		parcel.writeValue(id)
		parcel.writeString(email)
		parcel.writeString(username)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ListEmployeeItem> {
		override fun createFromParcel(parcel: Parcel): ListEmployeeItem {
			return ListEmployeeItem(parcel)
		}

		override fun newArray(size: Int): Array<ListEmployeeItem?> {
			return arrayOfNulls(size)
		}
	}
}

