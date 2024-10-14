package com.example.oasis.model

import java.io.Serializable

class Comprador(
    private var idUsuario: Int,
    private var nombre: String,
    private var email: String,
    private var contrasena: String,
    private var direcciones : MutableList<Ubicacion>
) : Serializable{

    fun agregarDireccion(direccion: Ubicacion){
        direcciones.add(direccion)
    }

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

    fun getDirecciones(): MutableList<Ubicacion> {
        return direcciones
    }

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

    fun setDirecciones(direcciones: MutableList<Ubicacion>) {
        this.direcciones = direcciones
    }
}