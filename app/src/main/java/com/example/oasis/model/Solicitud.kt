package com.example.oasis.model

import java.io.Serializable
import java.time.LocalDateTime

class Solicitud(
    private var id: Int,
    private var ordenes: List<Order>,
    private var total: Double,
    private var fecha: LocalDateTime,
    private var estado: String,
    private var direccion: String
): Serializable {
    fun getId(): Int{
        return id
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
        return estado
    }

    fun getDireccion(): String{
        return direccion
    }

    fun setId(id: Int){
        this.id = id
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
        this.estado = estado
    }
}