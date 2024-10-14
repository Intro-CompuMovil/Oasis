package com.example.oasis.model

import java.io.Serializable

class Product(
    private var idProducto: Int,
    private var nombre: String,
    private var descripcion: String,
    private var puntaje: Float,
    private var precio: Double,
    private var categoria: String,
    private var imagen: String,
    private var productoLatitud: Double,
    private var productoLongitud: Double
):Serializable {
    // Getters
    fun getId(): Int {
        return idProducto
    }
    fun getNombre(): String {
        return nombre
    }

    fun getDescripcion(): String {
        return descripcion
    }

    fun getPuntaje(): Float {
        return puntaje
    }

    fun getPrecio(): Double {
        return precio
    }

    fun getCategoria(): String {
        return categoria
    }

    fun getImagen(): String {
        return imagen
    }

    fun getProductoLatitud(): Double {
        return productoLatitud
    }

    fun getProductoLongitud(): Double {
        return productoLongitud
    }

    // Setters
    fun setId(id: Int) {
        this.idProducto = id
    }
    fun setNombre(nombre: String) {
        this.nombre = nombre
    }

    fun setDescripcion(descripcion: String) {
        this.descripcion = descripcion
    }

    fun setPuntaje(puntaje: Float) {
        this.puntaje = puntaje
    }

    fun setPrecio(precio: Double) {
        this.precio = precio
    }

    fun setCategoria(categoria: String) {
        this.categoria = categoria
    }

    fun setImagen(imagen: String) {
        this.imagen = imagen
    }

    fun setProductoLatitud(productoLatitud: Double) {
        this.productoLatitud = productoLatitud
    }

    fun setProductoLongitud(productoLongitud: Double) {
        this.productoLongitud = productoLongitud
    }
}