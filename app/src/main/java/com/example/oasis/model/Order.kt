package com.example.oasis.model

import java.io.Serializable

class Order (
    private var producto: Product,
    private var cantidad: Int
): Serializable{
    // Getters
    fun getProducto(): Product {
        return producto
    }

    fun getCantidad(): Int {
        return cantidad
    }

    // Setters
    fun setProducto(product: Product) {
        this.producto = product
    }

    fun setCantidad(quantity: Int) {
        this.cantidad = quantity
    }
}