package com.example.practica3.api

import com.example.practica3.models.CartProduct
import com.example.practica3.models.Product
import org.json.JSONObject

fun transformApiResponse(rawJson: String): List<CartProduct> {
    val originalJson = JSONObject(rawJson)
    val transformedList = mutableListOf<CartProduct>()

    // Expresión regular para extraer datos del producto en las claves
    val regex = """Product \[id=(\d+), name=(.+), price=(\d+\.?\d*)]""".toRegex()

    originalJson.keys().forEach { key ->
        val matchResult = regex.find(key)
        if (matchResult != null) {
            val (id, name, price) = matchResult.destructured
            val quantity = originalJson.getInt(key)

            // Crear un objeto CartProduct y agregarlo a la lista
            val cartProduct = CartProduct(
                product = Product(
                    id = id.toLong(),
                    name = name,
                    price = price.toDouble(),
                ),
                quantity = quantity,
            )
            transformedList.add(cartProduct)
        }
    }

    return transformedList
}


