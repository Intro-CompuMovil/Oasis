package com.example.oasis.logica.db

import android.content.Context
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.LocalDateTimeAdapter
import com.example.oasis.model.Comprador
import com.example.oasis.model.Product
import com.example.oasis.model.Repartidor
import com.example.oasis.model.Solicitud
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

class DataBaseSimulator(private val context: Context) {
    private val productsJson = "productos.json"
    private val compradoresJson = "compradores.json"
    private val repartidoresJson = "repartidores.json"
    private val solicitudesJson = "solicitudes.json"
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    fun getProducts(): MutableList<Product> {
        val jsonFileString = AppUtilityHelper.loadJSONFromAsset(context, productsJson)
        val listProductType = object : TypeToken<MutableList<Product>>() {}.type
        return gson.fromJson(jsonFileString, listProductType)
    }

    fun loginComprador(email: String, password: String): Comprador? {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, compradoresJson)
        val listCompradorType = object : TypeToken<MutableList<Comprador>>() {}.type
        val compradores = gson.fromJson<MutableList<Comprador>>(jsonFileString, listCompradorType)
        val comprador = compradores.find { it.getEmail() == email && it.getContrasena() == password }
        return comprador
    }

    fun registerComprador(comprador: Comprador): Boolean {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, compradoresJson)
        val listCompradorType = object : TypeToken<MutableList<Comprador>>() {}.type
        val compradores = gson.fromJson<MutableList<Comprador>>(jsonFileString, listCompradorType)
        if (compradores.any { it.getEmail() == comprador.getEmail() }) {
            return false
        }
        compradores.add(comprador)
        val jsonString = gson.toJson(compradores)
        AppUtilityHelper.saveJSONToExternalStorage(context, compradoresJson, jsonString)
        return true
    }

    fun actualizarComprador(comprador: Comprador): Boolean {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, compradoresJson)
        val listCompradorType = object : TypeToken<MutableList<Comprador>>() {}.type
        val compradores = gson.fromJson<MutableList<Comprador>>(jsonFileString, listCompradorType)
        val index = compradores.indexOfFirst { it.getEmail() == comprador.getEmail() }
        if (index == -1) {
            return false
        }
        compradores[index] = comprador
        val jsonString = gson.toJson(compradores)
        AppUtilityHelper.saveJSONToExternalStorage(context, compradoresJson, jsonString)
        return true
    }

    fun loginRepartidor(email: String, password: String): Repartidor? {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, repartidoresJson)
        val listCompradorType = object : TypeToken<MutableList<Repartidor>>() {}.type
        val repartidores = gson.fromJson<MutableList<Repartidor>>(jsonFileString, listCompradorType)
        val repartidor = repartidores.find { it.getEmail() == email && it.getContrasena() == password }
        return repartidor
    }

    fun registerRepartidor(repartidor: Repartidor): Boolean {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, repartidoresJson)
        val listCompradorType = object : TypeToken<MutableList<Repartidor>>() {}.type
        val repartidores = gson.fromJson<MutableList<Repartidor>>(jsonFileString, listCompradorType)
        if (repartidores.any { it.getEmail() == repartidor.getEmail() }) {
            return false
        }
        repartidores.add(repartidor)
        val jsonString = gson.toJson(repartidores)
        AppUtilityHelper.saveJSONToExternalStorage(context, repartidoresJson, jsonString)
        return true
    }

    fun actualizarRepartidor(repartidor: Repartidor): Boolean {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, repartidoresJson)
        val listCompradorType = object : TypeToken<MutableList<Repartidor>>() {}.type
        val repartidores = gson.fromJson<MutableList<Repartidor>>(jsonFileString, listCompradorType)
        val index = repartidores.indexOfFirst { it.getEmail() == repartidor.getEmail() }
        if (index == -1) {
            return false
        }
        repartidores[index] = repartidor
        val jsonString = gson.toJson(repartidores)
        AppUtilityHelper.saveJSONToExternalStorage(context, repartidoresJson, jsonString)
        return true
    }

    fun getSolicitudesActivas(): MutableList<Solicitud> {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, solicitudesJson)
        val listSolicitudType = object : TypeToken<MutableList<Solicitud>>() {}.type
        val solicitudes = gson.fromJson<MutableList<Solicitud>>(jsonFileString, listSolicitudType)
        return solicitudes.filter { it.getEstado() == "No entregado" && it.getRepartidor()==null}.toMutableList()
    }

    fun getSolicitudesByUser(email: String): MutableList<Solicitud> {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, solicitudesJson)
        val listSolicitudType = object : TypeToken<MutableList<Solicitud>>() {}.type
        val solicitudes = gson.fromJson<MutableList<Solicitud>>(jsonFileString, listSolicitudType)
        return solicitudes.filter {it.getComprador().getEmail() == email ||
                (it.getRepartidor()?.getEmail() == email)}.toMutableList()
    }

    fun addSolicitud(solicitud: Solicitud): Boolean {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, solicitudesJson)
        val listSolicitudType = object : TypeToken<MutableList<Solicitud>>() {}.type
        val solicitudes = gson.fromJson<MutableList<Solicitud>>(jsonFileString, listSolicitudType)
        solicitudes.add(solicitud)
        val jsonString = gson.toJson(solicitudes)
        AppUtilityHelper.saveJSONToExternalStorage(context, solicitudesJson, jsonString)
        return true
    }

    fun actualizarSolicitud(solicitud: Solicitud): Boolean {
        val jsonFileString = AppUtilityHelper.loadJSONFromExternalStorage(context, solicitudesJson)
        val listSolicitudType = object : TypeToken<MutableList<Solicitud>>() {}.type
        val solicitudes = gson.fromJson<MutableList<Solicitud>>(jsonFileString, listSolicitudType)
        val index = solicitudes.indexOfFirst { it.getId() == solicitud.getId() }
        if (index == -1) {
            return false
        }
        solicitudes[index] = solicitud
        val jsonString = gson.toJson(solicitudes)
        AppUtilityHelper.saveJSONToExternalStorage(context, solicitudesJson, jsonString)
        return true
    }
}