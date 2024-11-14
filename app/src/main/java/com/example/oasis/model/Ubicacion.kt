package com.example.oasis.model

import java.io.Serializable

class Ubicacion(
    private var latitud: Double,
    private var longitud: Double,
    private var direccion: String
): Serializable {

    constructor(): this(
        0.0,
        0.0,
        ""
    )

    fun getLatitud(): Double{
        return latitud
    }

    fun getLongitud(): Double{
        return longitud
    }

    fun getDireccion(): String{
        return direccion
    }

    fun setLatitud(latitud: Double){
        this.latitud = latitud
    }

    fun setLongitud(longitud: Double){
        this.longitud = longitud
    }

    fun setDirección(dirección: String){
        this.direccion = dirección
    }
}