package com.naffeid.gassin.data.remote.api

import com.naffeid.gassin.data.remote.response.CustomerResponse
import com.naffeid.gassin.data.remote.response.EmployeeResponse
import com.naffeid.gassin.data.remote.response.LoginResponse
import com.naffeid.gassin.data.remote.response.MessageResponse
import com.naffeid.gassin.data.remote.response.PurchaseTransactionResponse
import com.naffeid.gassin.data.remote.response.ResupplyTransactionResponse
import com.naffeid.gassin.data.remote.response.SingleCustomerResponse
import com.naffeid.gassin.data.remote.response.SingleEmployeeResponse
import com.naffeid.gassin.data.remote.response.SinglePurchaseResponse
import com.naffeid.gassin.data.remote.response.SingleResupplyResponse
import com.naffeid.gassin.data.remote.response.SingleStoreResponse
import com.naffeid.gassin.data.remote.response.SingleTransactionResponse
import com.naffeid.gassin.data.remote.response.StoreResponse
import com.naffeid.gassin.data.remote.response.TransactionResponse
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
    @GET("manager/employee")
    suspend fun showAllEmployee(): EmployeeResponse

    @FormUrlEncoded
    @POST("manager/employee")
    suspend fun createNewEmployee(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String
    ): MessageResponse

    @GET("manager/employee/{id}")
    suspend fun showEmployee(
        @Path("id") id: String
    ): SingleEmployeeResponse

    @FormUrlEncoded
    @PUT("manager/employee/{id}")
    suspend fun updateEmployee(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String?,
        @Field("phone") phone: String
    ): SingleEmployeeResponse

    @FormUrlEncoded
    @PUT("manager/employee/{id}")
    suspend fun updateEmployeeWithoutPassword(
        @Path("id") id: String,
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("phone") phone: String
    ): SingleEmployeeResponse

    @DELETE("manager/employee/{id}")
    suspend fun deleteEmployee(
        @Path("id") id: String
    ): EmployeeResponse

    @GET("manager/employees/search")
    suspend fun searchEmployee(
        @Query("q") query: String
    ): EmployeeResponse

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

    @GET("manager/stores/search")
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
        @Field("link_map") linkMap: String,
        @Field("price") price: String
    ): SingleCustomerResponse

    @DELETE("manager/customer/{id}")
    suspend fun deleteCustomer(
        @Path("id") id: String
    ): CustomerResponse

    @GET("manager/customers/search")
    suspend fun searchCustomer(
        @Query("q") query: String
    ): CustomerResponse

    /* End API Customer */

    /* Purchase Transaction */
    @GET("manager/purchase")
    suspend fun showAllPurchaseTransaction(): PurchaseTransactionResponse

    @FormUrlEncoded
    @POST("manager/purchase")
    suspend fun createNewPurchaseTransaction(
        @Field("id_customer") idCustomer: String,
        @Field("id_user") idUser: String,
        @Field("qty") qty: String,
        @Field("total_payment") totalPayment: String
    ): MessageResponse

    @GET("manager/purchase/{id}")
    suspend fun showPurchaseTransaction(
        @Path("id") id: String
    ): SinglePurchaseResponse

    @FormUrlEncoded
    @PUT("manager/purchase/{id}")
    suspend fun updatePurchaseTransaction(
        @Path("id") id: String,
        @Field("id_customer") idCustomer: String,
        @Field("id_user") idUser: String,
        @Field("qty") qty: String,
        @Field("total_payment") totalPayment: String,
        @Field("status") status: String,
        @Field("note") note: String
    ): SinglePurchaseResponse

    @DELETE("manager/purchase/{id}")
    suspend fun deletePurchaseTransaction(
        @Path("id") id: String
    ): PurchaseTransactionResponse

    @GET("manager/purchases/search")
    suspend fun searchPurchaseTransaction(
        @Query("q") query: String
    ): PurchaseTransactionResponse

    /* End API Purchase */

    /* Resupply Transaction */
    @GET("manager/resupply")
    suspend fun showAllResupplyTransaction(): ResupplyTransactionResponse

    @FormUrlEncoded
    @POST("manager/resupply")
    suspend fun createNewResupplyTransaction(
        @Field("id_store") idStore: String,
        @Field("id_user") idUser: String,
        @Field("qty") qty: String,
        @Field("total_payment") totalPayment: String
    ): MessageResponse

    @GET("manager/resupply/{id}")
    suspend fun showResupplyTransaction(
        @Path("id") id: String
    ): SingleResupplyResponse

    @FormUrlEncoded
    @PUT("manager/resupply/{id}")
    suspend fun updateResupplyTransaction(
        @Path("id") id: String,
        @Field("id_store") idStore: String,
        @Field("id_user") idUser: String,
        @Field("qty") qty: String,
        @Field("total_payment") totalPayment: String,
        @Field("status") status: String,
        @Field("note") note: String
    ): SingleResupplyResponse

    @DELETE("manager/resupply/{id}")
    suspend fun deleteResupplyTransaction(
        @Path("id") id: String
    ): ResupplyTransactionResponse

    @GET("manager/resupplys/search")
    suspend fun searchResupplyTransaction(
        @Query("q") query: String
    ): ResupplyTransactionResponse

    /* End API Resupply */

    /* Transaction */
    @GET("employee/transaction")
    suspend fun showAllTransaction(): TransactionResponse

    @GET("employee/transaction/{id}")
    suspend fun showTransaction(
        @Path("id") id: String,
        @Query("type") type: String
    ): SingleTransactionResponse

    /* End API Transaction */
}