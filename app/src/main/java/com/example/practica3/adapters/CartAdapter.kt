package com.example.practica3.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practica3.api.ApiService
import com.example.practica3.models.CartProduct
import com.example.practica3.R
import com.example.practica3.api.transformApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartAdapter(
    private var cartList: List<CartProduct>,
    private val apiService: ApiService
) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun updateData(newCartList: List<CartProduct>) {
        this.cartList = newCartList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartList[position]
        holder.textViewName.text = product.product.name
        holder.textViewPrice.text = "$${product.product.price}"
        holder.textViewQuantity.text = "${product.quantity}"

        holder.buttonRemoveFromCart.setOnClickListener {
            removeFromCart(product.product.id)
        }
    }

    override fun getItemCount(): Int = cartList.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)
        val buttonRemoveFromCart: Button = itemView.findViewById(R.id.buttonRemoveCart)

    }

    private fun removeFromCart(productId: Long) {
        apiService.removeCartProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("RemoveFromCart", "Producto eliminado del carrito correctamente")
                    reloadCartData()
                } else {
                    Log.e("RemoveFromCart", "Error al eliminar producto del carrito: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RemoveFromCart", "Fallo al eliminar producto del carrito: ${t.message}")
            }
        })
    }

    private fun reloadCartData() {
        apiService.getCartProducts().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val rawJson = response.body()?.string()
                    rawJson?.let {
                        val productList = transformApiResponse(it)
                        updateData(productList)
                    }
                } else {
                    Log.e("ReloadCart", "Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("ReloadCart", "Failure: ${t.message}")
            }
        })
    }
}

