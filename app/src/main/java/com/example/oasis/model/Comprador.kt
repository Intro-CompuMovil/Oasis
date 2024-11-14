package com.example.oasis.model

import java.io.Serializable

class Comprador(
    private var idUsuario: String,
    private var nombre: String,
    private var email: String,
    private var contrasena: String,
    private var direcciones : MutableList<Ubicacion>
) : Serializable{

    constructor() : this(
        "",
        "",
        "",
        "",
        mutableListOf()
    )

    fun agregarDireccion(direccion: Ubicacion){
        direcciones.add(direccion)
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

    fun getDirecciones(): List<Ubicacion> {
        return direcciones.toList()
    }

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

    fun setDirecciones(direcciones: MutableList<Ubicacion>) {
        this.direcciones = direcciones
    }
}