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
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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
    private lateinit var adminAdapter: AdminAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutAddProduct: LinearLayout
    private lateinit var buttonCart: Button
    private lateinit var buttonCatalog: Button
    private lateinit var buttonAdmin: Button
    private lateinit var buttonAddProduct: Button
    private lateinit var buttonSubmitProduct: Button
    private lateinit var buttonBackCatalog: Button
    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductPrice: EditText
    private lateinit var headerTitle: TextView
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        buttonAdmin = findViewById(R.id.buttonAdmin)
        buttonCatalog = findViewById(R.id.buttonCatalog)
        recyclerView = findViewById(R.id.recyclerViewProducts)
        layoutAddProduct = findViewById(R.id.layoutAddProduct)
        headerTitle = findViewById(R.id.headerTitle)
        buttonCart = findViewById(R.id.buttonCart)
        buttonAddProduct = findViewById(R.id.buttonAddProduct)
        buttonSubmitProduct = findViewById(R.id.buttonSubmitProduct)
        editTextProductName = findViewById(R.id.editTextProductName)
        editTextProductPrice = findViewById(R.id.editTextProductPrice)
        buttonBackCatalog = findViewById(R.id.buttonBack)

        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList(), apiService)
        cartAdapter = CartAdapter(emptyList(), apiService)
        adminAdapter = AdminAdapter(emptyList(), apiService)

        // Inicializar con la vista del catálogo
        fetchProducts()

        // Botón para alternar entre el carrito y el catálogo
        buttonCart.setOnClickListener {
            fetchCartProducts()
            headerTitle.text = "Cart"
        }

        buttonCatalog.setOnClickListener {
            fetchProducts()
            headerTitle.text = "Catalog"
        }

        buttonAdmin.setOnClickListener {
            fetchAdmin()
            headerTitle.text = "Admin"
        }

        buttonAddProduct.setOnClickListener {
            showAddProductView()
        }

        buttonBackCatalog.setOnClickListener {
            showCatalogView()
        }

        buttonSubmitProduct.setOnClickListener {
            val productName = editTextProductName.text.toString()
            val productPrice = editTextProductPrice.text.toString().toDoubleOrNull()

            if (productName.isNotBlank() && productPrice != null) {
                addProduct(productName, productPrice)
            } else {
                Log.e("AddProduct", "Invalid input")
            }
        }

    }

    private fun showAddProductView() {
        recyclerView.visibility = View.GONE
        layoutAddProduct.visibility = View.VISIBLE
        headerTitle.text = "Add Product"
        buttonBackCatalog.visibility = View.VISIBLE
        buttonCart.visibility = View.GONE
        buttonAddProduct.visibility = View.GONE
        buttonCatalog.visibility = View.GONE
        buttonAdmin.visibility = View.GONE
    }

    private fun showCatalogView() {
        layoutAddProduct.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        headerTitle.text = "Catalog"
        buttonCart.visibility = View.VISIBLE
        buttonAddProduct.visibility = View.VISIBLE
        buttonBackCatalog.visibility = View.GONE
        buttonCatalog.visibility = View.VISIBLE
        buttonAdmin.visibility = View.VISIBLE
        fetchProducts()
    }

    private fun fetchProducts() {
        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    productList?.let {
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
                        val productList = transformApiResponse(it)
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

    private fun fetchAdmin() {
        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    productList?.let {
                        adminAdapter = AdminAdapter(it, apiService)
                        recyclerView.adapter = adminAdapter
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

    private fun addProduct(name: String, price: Double) {
        apiService.addProduct(name, price).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("AddProduct", "Product added successfully")
                    showCatalogView() // Volver al catálogo
                } else {
                    Log.e("AddProduct", "Error adding product: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("AddProduct", "Error: ${t.message}")
            }
        })
    }

}
