package com.example.practica3.adapters
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.example.practica3.api.ApiService
import com.example.practica3.models.Product
import com.example.practica3.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductAdapter(
    private var productList: List<Product>,
    private val apiService: ApiService
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateData(newProductList: List<Product>) {
        this.productList = newProductList
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val buttonAddToCart: Button = itemView.findViewById(R.id.buttonAddCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.textViewName.text = product.name
        holder.textViewPrice.text = "$${product.price}"

        holder.buttonAddToCart.setOnClickListener {
            addToCart(product.id)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    private fun addToCart(productId: Long) {
        apiService.addCartProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Mostrar un mensaje de éxito (opcional)
                    Log.d("AddToCart", "Producto añadido correctamente")
                } else {
                    Log.e("AddToCart", "Error al añadir producto: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("AddToCart", "Fallo al añadir producto: ${t.message}")
            }
        })
    }
}
