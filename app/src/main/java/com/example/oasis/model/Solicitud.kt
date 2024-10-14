package com.example.oasis.model

import java.io.Serializable
import java.time.LocalDateTime

class Solicitud(
    private var idSolicitud: Int,
    private var ordenes: List<Order>,
    private var total: Double,
    private var fecha: LocalDateTime,
    private var estadoSolicitud: String,
    private var ubicacion: Ubicacion,
    private var comprador: Comprador,
    private var repartidor: Repartidor?,
    private var repartidorLatitud: Double?,
    private var repartidorLongitud: Double?
): Serializable {
    fun getId(): Int{
        return idSolicitud
    }

    fun getOrdenes(): List<Order>{
        return ordenes
    }

    fun getTotal(): Double{
        return total
    }

    fun getFecha(): LocalDateTime{
        return fecha
    }

    fun getEstado(): String{
        return estadoSolicitud
    }

    fun getUbicacion(): Ubicacion{
        return ubicacion
    }

    fun getComprador(): Comprador{
        return comprador
    }

    fun getRepartidor(): Repartidor?{
        return repartidor
    }

    fun getRepartidorLatitud(): Double?{
        return repartidorLatitud
    }

    fun getRepartidorLongitud(): Double?{
        return repartidorLongitud
    }

    fun setId(id: Int){
        this.idSolicitud = id
    }

    fun setOrdenes(ordenes: List<Order>){
        this.ordenes = ordenes
    }

    fun setTotal(total: Double){
        this.total = total
    }

    fun setFecha(fecha: LocalDateTime){
        this.fecha = fecha
    }

    fun setEstado(estado: String){
        this.estadoSolicitud = estado
    }

    fun setUbicacion(direccion: Ubicacion){
        this.ubicacion = direccion
    }

    fun setComprador(comprador: Comprador){
        this.comprador = comprador
    }

    fun setRepartidor(repartidor: Repartidor?){
        this.repartidor = repartidor
    }

    fun setRepartidorLatitud(repartidorLatitud: Double?){
        this.repartidorLatitud = repartidorLatitud
    }

    fun setRepartidorLongitud(repartidorLongitud: Double?){
        this.repartidorLongitud = repartidorLongitud
    }
}