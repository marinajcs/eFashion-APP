package com.example.practica3
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("api/auth/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @GET("api/products")
    fun getAllProducts(): Call<List<Product>>

    @Headers("Content-Type: application/json")
    @GET("api/cart")
    fun getCartProducts(): Call<ResponseBody>

    // Añadir un producto al carrito
    @POST("api/cart/add")
    fun addCartProduct(
        @Query("productId") productId: Long

    ): Call<Void>

    // Eliminar un producto del carrito
    @POST("api/cart/delete")
    fun removeCartProduct(
        @Query("productId") productId: Long
    ): Call<Void>

    // Añadir un producto al catálogo
    @POST("api/products/add")
    fun addProduct(
        @Query("name") name: String,
        @Query("price") price: Double
    ): Call<Void>

    // Editar un product como admin
    @POST("api/products/edit")
    fun editProduct(
        @Query("productId") productId: Long,
        @Query("name") name: String,
        @Query("price") price: Double
    ): Call<Void>

    // Eliminar un producto como admin
    @POST("api/products/delete")
    fun deleteProduct(
        @Query("productId") productId: Long
    ): Call<Void>

    // Consultar el precio total
    @Headers("Content-Type: application/json")
    @GET("api/cart/total")
    fun getTotalPrice(): Call<Double>

    // Consultar la factura
    @Headers("Content-Type: application/json")
    @GET("api/cart/bill")
    fun billExport(): Call<ResponseBody>
}