package com.example.practica3
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAdapter(
    private var productList: List<Product>,
    private val apiService: ApiService,
    private val onEditProduct: (Product) -> Unit

) : RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

    fun updateData(newProductList: List<Product>) {
        this.productList = newProductList
        notifyDataSetChanged()
    }

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val buttonDeleteProduct: Button = itemView.findViewById(R.id.buttonDeleteProduct)
        val buttonEditProduct: Button = itemView.findViewById(R.id.buttonEditProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val product = productList[position]
        holder.textViewName.text = product.name
        holder.textViewPrice.text = "$${product.price}"

        holder.buttonEditProduct.setOnClickListener {
            onEditProduct(product)
        }

        holder.buttonDeleteProduct.setOnClickListener {
            deleteProduct(product.id)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    private fun deleteProduct(productId: Long) {
        apiService.deleteProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("DeleteProduct", "Product deleted successfully")
                    reloadProducts()
                } else {
                    Log.e("DeleteProduct", "Error deleting product: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DeleteProduct", "Error: ${t.message}")
            }
        })
    }

    private fun reloadProducts() {
        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val productList = response.body()
                    if (productList != null) {
                        updateData(productList)
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
}
