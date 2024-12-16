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
import android.os.Environment
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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    // Login y logout
    private lateinit var layoutLogin: LinearLayout
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonLogout: Button

    // Adaptadores
    private lateinit var productAdapter: ProductAdapter
    private lateinit var cartAdapter: CartAdapter
    private lateinit var adminAdapter: AdminAdapter

    // Visualizar datos
    private lateinit var recyclerView: RecyclerView
    private lateinit var headerTitle: TextView

    // Añadir productos
    private lateinit var layoutAddProduct: LinearLayout
    private lateinit var buttonAddProduct: Button
    private lateinit var buttonSubmitProduct: Button
    private lateinit var buttonBackCatalog: Button
    private lateinit var editTextProductName: EditText
    private lateinit var editTextProductPrice: EditText

    // Editar productos:
    private lateinit var layoutEditProduct: LinearLayout
    private lateinit var editTextEditName: EditText
    private lateinit var editTextEditPrice: EditText
    private lateinit var buttonSaveEdit: Button
    private lateinit var buttonCancelEdit: Button

    // Confirmación de compra
    private lateinit var layoutCartSummary: LinearLayout
    private lateinit var textViewTotalPrice: TextView
    private lateinit var buttonCheckout: Button

    // Menú inferior
    private lateinit var layoutButtons: LinearLayout
    private lateinit var buttonCart: Button
    private lateinit var buttonCatalog: Button
    private lateinit var buttonAdmin: Button
    private lateinit var buttonMap: Button

    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Login y logout
        layoutLogin = findViewById(R.id.layoutLogin)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Menú inferior
        layoutButtons =findViewById(R.id.layoutButtons)
        buttonCatalog = findViewById(R.id.buttonCatalog)
        buttonCart = findViewById(R.id.buttonCart)
        buttonAdmin = findViewById(R.id.buttonAdmin)
        buttonMap = findViewById(R.id.buttonMap)

        // Visualizar productos
        headerTitle = findViewById(R.id.headerTitle)
        recyclerView = findViewById(R.id.recyclerViewProducts)

        // Añadir productos
        layoutAddProduct = findViewById(R.id.layoutAddProduct)
        buttonAddProduct = findViewById(R.id.buttonAddProduct)
        buttonSubmitProduct = findViewById(R.id.buttonSubmitProduct)
        editTextProductName = findViewById(R.id.editTextProductName)
        editTextProductPrice = findViewById(R.id.editTextProductPrice)
        buttonBackCatalog = findViewById(R.id.buttonBack)

        // Editar productos
        layoutEditProduct = findViewById(R.id.layoutEditProduct)
        editTextEditName = findViewById(R.id.editTextEditName)
        editTextEditPrice = findViewById(R.id.editTextEditPrice)
        buttonSaveEdit = findViewById(R.id.buttonSaveEdit)
        buttonCancelEdit = findViewById(R.id.buttonCancelEdit)

        // Confirmación de compra
        layoutCartSummary = findViewById(R.id.layoutCartSummary)
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice)
        buttonCheckout = findViewById(R.id.buttonCheckout)

        // Adaptadores
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList(), apiService)
        cartAdapter = CartAdapter(emptyList(), apiService)
        adminAdapter = AdminAdapter(emptyList(), apiService) { product ->
            showEditProductView(product)
        }

        showLogin()

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                fetchLogin(username, password)
            } else {
                Log.e("Login", "Username or password cannot be empty")
            }
        }

        buttonLogout.setOnClickListener {
            fetchLogout()
            showLogin()
        }

        // Funcionalidad del menú inferior
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

        buttonMap.setOnClickListener {
            recyclerView.adapter = MapAdapter(this)
            headerTitle.text = "Map"
            buttonAddProduct.visibility = View.GONE
        }

        // Añadir productos
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

        // Editar productos
        buttonSaveEdit.setOnClickListener {
            val newName = editTextEditName.text.toString()
            val newPrice = editTextEditPrice.text.toString().toDoubleOrNull()

            if (newName.isNotBlank() && newPrice != null) {
                saveEditedProduct(currentEditingProductId, newName, newPrice)
            } else {
                Log.e("EditProduct", "Invalid input")
            }
        }

        buttonCancelEdit.setOnClickListener {
            showAdminView()
        }

        // Realización de compra
        buttonCheckout.setOnClickListener {
            val confirmationDialog = android.app.AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage("Do you want to proceed with the payment?")
                .setPositiveButton("Yes") { _, _ ->
                    billExport()
                }
                .setNegativeButton("No", null)
                .create()

            confirmationDialog.show()
        }

    }

    // Funciones del añadido de productos
    private fun addProduct(name: String, price: Double) {
        apiService.addProduct(name, price).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("AddProduct", "Product added successfully")
                    showCatalogView()
                } else {
                    Log.e("AddProduct", "Error adding product: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("AddProduct", "Error: ${t.message}")
            }
        })
    }

    private fun showLogin() {
        headerTitle.text = "Login"
        layoutLogin.visibility = View.VISIBLE
        layoutButtons.visibility = View.GONE
        layoutCartSummary.visibility = View.GONE
        buttonAddProduct.visibility = View.GONE
    }

    private fun showAddProductView() {
        headerTitle.text = "Add Product"
        layoutAddProduct.visibility = View.VISIBLE
        buttonBackCatalog.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        buttonCart.visibility = View.GONE
        buttonAddProduct.visibility = View.GONE
        buttonCatalog.visibility = View.GONE
        buttonAdmin.visibility = View.GONE
        buttonMap.visibility = View.GONE
        layoutCartSummary.visibility = View.GONE
    }

    private fun showCatalogView() {
        headerTitle.text = "Catalog"
        layoutCartSummary.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        layoutButtons.visibility = View.VISIBLE
        buttonAddProduct.visibility = View.GONE
        layoutAddProduct.visibility = View.GONE
        buttonBackCatalog.visibility = View.GONE
    }

    // Funciones del editado de productos
    private var currentEditingProductId: Long = 0

    private fun saveEditedProduct(productId: Long, name: String, price: Double) {
        apiService.editProduct(productId, name, price).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("EditProduct", "Product edited successfully")
                    showAdminView()
                } else {
                    Log.e("EditProduct", "Error editing product: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("EditProduct", "Error: ${t.message}")
            }
        })
    }

    private fun showEditProductView(product: Product) {
        currentEditingProductId = product.id
        editTextEditName.setText(product.name)
        editTextEditPrice.setText(product.price.toString())

        headerTitle.text = "Edit Product"
        layoutEditProduct.visibility = View.VISIBLE
        buttonSaveEdit.visibility = View.VISIBLE
        buttonCancelEdit.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        buttonCart.visibility = View.GONE
        buttonAddProduct.visibility = View.GONE
        buttonCatalog.visibility = View.GONE
        buttonAdmin.visibility = View.GONE
        layoutCartSummary.visibility = View.GONE
    }

    private fun showAdminView() {
        headerTitle.text = "Admin"
        buttonAddProduct.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        buttonCart.visibility = View.VISIBLE
        buttonCatalog.visibility = View.VISIBLE
        buttonAdmin.visibility = View.VISIBLE
        layoutEditProduct.visibility = View.GONE
        buttonSaveEdit.visibility = View.GONE
        buttonCancelEdit.visibility = View.GONE
        layoutCartSummary.visibility = View.GONE
    }

    private fun checkUserRoleAndSetView() {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val userRole = sharedPreferences.getString("userRole", "USER")

        if (userRole == "ROLE_admin") {
            buttonAdmin.visibility = View.VISIBLE
        } else {
            buttonAdmin.visibility = View.GONE
        }
    }

    // Funciones de la realización de la compra
    private fun getTotalPrice() {
        apiService.getTotalPrice().enqueue(object : Callback<Double> {
            override fun onResponse(call: Call<Double>, response: Response<Double>) {
                if (response.isSuccessful) {
                    val totalPrice = response.body() ?: 0.0
                    textViewTotalPrice.text = "Total Price: $${"%.2f".format(totalPrice)}"
                    layoutCartSummary.visibility = View.VISIBLE
                } else {
                    Log.e("TotalPrice", "Error fetching total price: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Double>, t: Throwable) {
                Log.e("TotalPrice", "Failure: ${t.message}")
            }
        })
    }

    private fun billExport() {
        apiService.billExport().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        try {
                            val file = saveFileToStorage(responseBody)
                            Log.d("Checkout", "Bill generated successfully: ${file.absolutePath}")
                            val successDialog = android.app.AlertDialog.Builder(this@MainActivity)
                                .setTitle("Invoice Downloaded")
                                .setMessage("Your bill has been successfully downloaded in Downloads.")
                                .setPositiveButton("OK", null)
                                .create()
                            successDialog.show()

                            showCatalogView()
                        } catch (e: IOException) {
                            Log.e("Checkout", "Error saving invoice: ${e.message}")
                        }
                    } else {
                        Log.e("Checkout", "Response body is null")
                    }
                } else {
                    Log.e("Checkout", "Error generating invoice: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Checkout", "Error: ${t.message}")
            }
        })
    }

    @Throws(IOException::class)
    private fun saveFileToStorage(responseBody: ResponseBody): File {
        val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDirectory, "bill.pdf")

        val inputStream: InputStream = responseBody.byteStream()
        val outputStream: OutputStream = FileOutputStream(file)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
        return file
    }

    // Consultas de listados
    private fun fetchProducts() {
        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    productList?.let {
                        productAdapter = ProductAdapter(it, apiService)
                        recyclerView.adapter = productAdapter
                        showCatalogView()
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
                        getTotalPrice()
                        buttonAddProduct.visibility = View.GONE
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
                        adminAdapter = AdminAdapter(it, apiService) { product ->
                            showEditProductView(product)
                        }
                        recyclerView.adapter = adminAdapter
                        showAdminView()
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

    private fun fetchLogin(username: String, password: String) {
        apiService.login(username, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        Log.d("Login", "Login successful: ${loginResponse.message}")
                        val userRole = loginResponse.role

                        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                        sharedPreferences.edit().putString("userRole", userRole).apply()

                        layoutLogin.visibility = View.GONE
                        recyclerView.visibility = View.GONE

                        checkUserRoleAndSetView()
                        fetchProducts()
                    }
                } else {
                    Log.e("Login", "Invalid credentials")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Login", "Error: ${t.message}")
            }
        })
    }

    private fun fetchLogout() {
        apiService.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Logout", "Logout successful")

                    layoutLogin.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    Log.e("Logout", "Logout failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Logout", "Error: ${t.message}")
            }
        })
    }
}
