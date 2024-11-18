package com.example.oasis.model

import java.io.Serializable

class Repartidor (
    private var idUsuario: String,
    private var nombre: String,
    private var email: String,
    private var contrasena: String,
    private var photoURL: String
) : Serializable {

    constructor(): this(
        "",
        "",
        "",
        "",
        ""

    )
    fun photoURL(): String {
        return photoURL
    }

    // Getters
    fun getId(): String {
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

    fun getPhotoURL(): String {
        return photoURL
    }

    // Setters

    // Setters

    fun setId(id: String) {
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

    fun setPhotoURL(photoURL: String) {
        this.photoURL = photoURL
    }

    fun copy(): Repartidor{
        return Repartidor(idUsuario, nombre, email, contrasena, photoURL)
    }
}