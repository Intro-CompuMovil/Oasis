package com.example.oasis.model

import java.io.Serializable

class Repartidor (
    private var idUsuario: Int,
    private var nombre: String,
    private var email: String,
    private var contrasena: String
) : Serializable {
    // Getters
    fun getId(): Int {
        return idUsuario
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

    // Setters

    fun setId(id: Int) {
        this.idUsuario = id
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