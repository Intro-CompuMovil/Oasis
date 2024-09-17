package com.example.oasis.model

import java.io.Serializable

class Product(
    private var id: Int,
    private var nombre: String,
    private var descripcion: String,
    private var puntaje: Float,
    private var precio: Double,
    private var categoria: String
):Serializable {
    // Getters
    fun getId(): Int {
        return id
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

    // Setters
    fun setId(id: Int) {
        this.id = id
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
}