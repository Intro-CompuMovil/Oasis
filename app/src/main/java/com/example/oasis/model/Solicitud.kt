package com.example.oasis.model

import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Solicitud(
    private var idSolicitud: String,
    private var ordenes: List<Order>,
    private var total: Double,
    private var fecha: String,
    private var estadoSolicitud: String,
    private var ubicacion: Ubicacion,
    private var comprador: Comprador,
    private var repartidor: Repartidor?,
    private var repartidorLatitud: Double?,
    private var repartidorLongitud: Double?
): Serializable {

    constructor(): this(
        "",
        mutableListOf(),
        0.0,
        "",
        "",
        Ubicacion(),
        Comprador(),
        null,
        null,
        null
    )

    constructor(
        idSolicitud: String,
        ordenes: List<Order>,
        total: Double,
        fecha: LocalDateTime,
        estadoSolicitud: String,
        ubicacion: Ubicacion,
        comprador: Comprador,
        repartidor: Repartidor?,
        repartidorLatitud: Double?,
        repartidorLongitud: Double?
    ) : this(
        idSolicitud,
        ordenes,
        total,
        fecha.format(DateTimeFormatter.ISO_DATE_TIME),  // Convierte a formato ISO-8601
        estadoSolicitud,
        ubicacion,
        comprador,
        repartidor,
        repartidorLatitud,
        repartidorLongitud
    )

    fun getId(): String{
        return idSolicitud
    }

    fun getOrdenes(): List<Order>{
        return ordenes
    }

    fun getTotal(): Double{
        return total
    }

    fun getFechaAsLocalDateTime(): LocalDateTime{
        return LocalDateTime.parse(fecha, DateTimeFormatter.ISO_DATE_TIME)
    }

    fun getFecha(): String{
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

    fun setId(id: String){
        this.idSolicitud = id
    }

    fun setOrdenes(ordenes: List<Order>){
        this.ordenes = ordenes
    }

    fun setTotal(total: Double){
        this.total = total
    }

    fun setFecha(fecha: String){
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