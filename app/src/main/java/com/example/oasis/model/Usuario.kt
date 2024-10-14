package com.example.oasis.model

import java.io.Serializable

open class Usuario(
    private var id: Int,
    private var nombre: String,
    private var email: String,
    private var contrasena: String
): Serializable {
    // Getters
    fun getId(): Int {
        return id
    }
    fun getNombre(): String {
        return nombre
    }

    fun getEmail(): String {
        return email
    }

    fun getContrasena(): String {
        return contrasena
    }

    // Setters
    fun setId(id: Int) {
        this.id = id
    }
    fun setNombre(nombre: String) {
        this.nombre = nombre
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setContrasena(contrasena: String) {
        this.contrasena = contrasena
    }
}