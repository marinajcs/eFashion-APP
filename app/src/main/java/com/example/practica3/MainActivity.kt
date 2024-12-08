package com.example.practica3

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.practica3.ui.theme.Practica3Theme
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tuapp.utils.transformApiResponse
import okhttp3.ResponseBody

class MainActivity : ComponentActivity() {
    private lateinit var productAdapter: ProductAdapter
    private lateinit var cartAdapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)
    private lateinit var buttonCart: Button
    private lateinit var buttonAddProduct: Button
    private lateinit var headerTitle: TextView
    private var isCartView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProducts)
        headerTitle = findViewById(R.id.headerTitle)
        buttonCart = findViewById(R.id.buttonCart)
        buttonAddProduct = findViewById(R.id.buttonAddProduct)

        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList(), apiService)
        cartAdapter = CartAdapter(emptyList(), apiService)

        // traer los datos del API
        fetchProducts()

        buttonCart.setOnClickListener {
            isCartView = !isCartView
            if (isCartView) {
                fetchCartProducts()
                buttonCart.text = "Catalog"
                headerTitle.text = "Cart"
            } else {
                fetchProducts()
                buttonCart.text = "Cart"
                headerTitle.text = "Catalog"
            }
        }
    }

    private fun fetchProducts() {

        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    Log.d("API_RESPONSE", "Productos: $productList")
                    productList?.let {
                        // Initialize the adapter with the product list
                        productAdapter = ProductAdapter(it, apiService)
                        recyclerView.adapter = productAdapter
                    }
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
            }
        })
    }

    private fun fetchCartProducts() {
        apiService.getCartProducts().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val rawJson = response.body()?.string()
                    rawJson?.let {
                        // Transforma el JSON en una lista de CartProduct
                        val productList = transformApiResponse(it)

                        Log.d("API_RESPONSE", "Productos: $productList")
                        cartAdapter = CartAdapter(productList, apiService)
                        recyclerView.adapter = cartAdapter
                    }
                } else {
                    Log.e("API_ERROR", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Failure: ${t.message}")
            }
        })
    }

}