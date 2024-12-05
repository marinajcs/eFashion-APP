/*package com.example.practica3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private var cartList: Map<Product, Int>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun updateData(newCartList: Map<Product, Int>) {
        this.cartList = newCartList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val productEntry = cartList.entries.toList()[position] // Convertimos el mapa a lista de entradas
        val product = productEntry.key
        val quantity = productEntry.value
        holder.bind(product, quantity)
    }

    override fun getItemCount(): Int = cartList.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        private val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity) // Asegúrate de agregar este TextView al layout

        fun bind(product: Product, quantity: Int) {
            textViewName.text = product.name
            textViewPrice.text = "Price: ${product.price}"
            textViewQuantity.text = "Quantity: $quantity"
        }
    }
}*/

package com.example.practica3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private var cartList: List<CartProduct>) :
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
    }

    override fun getItemCount(): Int = cartList.size

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewQuantity)

    }
}

