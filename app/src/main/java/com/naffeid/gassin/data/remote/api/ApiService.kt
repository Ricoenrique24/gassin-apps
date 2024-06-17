package com.naffeid.gassin.data.remote.api

import com.naffeid.gassin.data.remote.response.CustomerResponse
import com.naffeid.gassin.data.remote.response.LoginResponse
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.SingleCustomerResponse
import com.naffeid.gassin.data.remote.response.SingleStoreResponse
import com.naffeid.gassin.data.remote.response.StoreResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /* Authentication*/
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/getuser")
    suspend fun getUser(): LoginResponse

    @FormUrlEncoded
    @POST("auth/logout")
    suspend fun logout(): LoginResponse

    /* End API Authentication */

    /* Dashboard */
    @FormUrlEncoded
    @GET("dashboard/availablestockquantity")
    suspend fun availableStockQuantity(): LoginResponse
    @FormUrlEncoded
    @GET("dashboard/revenuetoday")
    suspend fun revenueToday(): LoginResponse
    @FormUrlEncoded
    @GET("dashboard/downloadtransactionrecap")
    suspend fun downloadTransactionRecap(): LoginResponse

    /* End API Dashboard */

    /* Employee */
    @FormUrlEncoded
    @GET("employee")
    suspend fun showAllEmployee(): LoginResponse
    @FormUrlEncoded
    @POST("employee")
    suspend fun createNewEmployee(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String
    ): LoginResponse
    @FormUrlEncoded
    @GET("employee/{id}")
    suspend fun showEmployee(
        @Path("id") id: String
    ): LoginResponse
    @FormUrlEncoded
    @PUT("employee/{id}")
    suspend fun updateEmployee(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String
    ): LoginResponse
    @FormUrlEncoded
    @DELETE("employee/{id}")
    suspend fun deleteEmployee(
        @Path("id") id: String
    ): LoginResponse

    /* End API Employee */

    /* Store */
    @GET("manager/store")
    suspend fun showAllStore(): StoreResponse
    @FormUrlEncoded
    @POST("manager/store")
    suspend fun createNewStore(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("link_map") linkMap: String,
        @Field("price") price: String
    ): MessageResponse
    @GET("manager/store/{id}")
    suspend fun showStore(
        @Path("id") id: String
    ): SingleStoreResponse
    @FormUrlEncoded
    @PUT("manager/store/{id}")
    suspend fun updateStore(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("linkmap") linkMap: String,
        @Field("price") price: String
    ): SingleStoreResponse
    @DELETE("manager/store/{id}")
    suspend fun deleteStore(
        @Path("id") id: String
    ): StoreResponse
    @GET("manager/search/stores")
    suspend fun searchStore(
        @Query("q") query: String
    ): StoreResponse

    /* End API Store */

    /* Customer */
    @GET("manager/customer")
    suspend fun showAllCustomer(): CustomerResponse
    @FormUrlEncoded
    @POST("manager/customer")
    suspend fun createNewCustomer(
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("link_map") linkMap: String,
        @Field("price") price: String
    ): MessageResponse
    @GET("manager/customer/{id}")
    suspend fun showCustomer(
        @Path("id") id: String
    ): SingleCustomerResponse
    @FormUrlEncoded
    @PUT("manager/customer/{id}")
    suspend fun updateCustomer(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("linkmap") linkMap: String,
        @Field("price") price: String
    ): SingleCustomerResponse
    @DELETE("manager/customer/{id}")
    suspend fun deleteCustomer(
        @Path("id") id: String
    ): CustomerResponse
    @GET("manager/search/customers")
    suspend fun searchCustomer(
        @Query("q") query: String
    ): CustomerResponse

    /* End API Customer */

}