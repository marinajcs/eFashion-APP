package com.dss.p3_dss
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/products")
    fun getAllProducts(): Call<List<Product>>
}