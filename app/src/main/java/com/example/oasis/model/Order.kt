package com.example.oasis.model

import java.io.Serializable

class Order (
    private var producto: Product,
    private var cantidad: Int,
    private var estadoOrden: String
): Serializable{

    constructor(): this(
        Product(),
        0,
        ""
    )

    // Getters
    fun getProducto(): Product {
        return producto
    }

    fun getCantidad(): Int {
        return cantidad
    }

    fun getEstadoOrden(): String {
        return estadoOrden
    }

    // Setters
    fun setProducto(product: Product) {
        this.producto = product
    }

    fun setCantidad(quantity: Int) {
        this.cantidad = quantity
    }

    fun setEstadoOrden(orderState: String) {
        this.estadoOrden = orderState
    }
}