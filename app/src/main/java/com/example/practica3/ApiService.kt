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
    // Get all products
    @Headers("Content-Type: application/json")
    @GET("api/products")
    fun getAllProducts(): Call<List<Product>>

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

    // Añadir un producto (POST request example)
    @POST("api/products/add")
    fun addProduct(
        @Query("name") name: String,
        @Query("price") price: Double
    ): Call<Void>

    // Editar un product por su ID
    @POST("api/products/edit/{id}")
    fun editProduct(
        @Path("id") id: Long,
        @Query("name") name: String,
        @Query("price") price: Double
    ): Call<Void>

    // Eliminar un producto por su ID
    @POST("api/products/delete/{id}")
    fun deleteProduct(
        @Path("id") id: Long
    ): Call<Void>

    @Headers("Content-Type: application/json")
    @GET("api/cart")
    fun getCartProducts(): Call<ResponseBody>
}