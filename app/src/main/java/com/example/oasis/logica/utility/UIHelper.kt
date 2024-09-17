package com.example.oasis.logica.utility

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.comprador.CompradorInicio
import com.example.oasis.logica.comprador.CompradorPerfil
import com.example.oasis.logica.comprador.CompradorSolicitudes
import com.example.oasis.logica.repartidor.RepartidorHistorial
import com.example.oasis.logica.repartidor.RepartidorInicio
import com.example.oasis.logica.repartidor.RepartidorPerfil


class UIHelper {
    fun setupFooter(activity: Activity) {
        val btnPedidos = activity.findViewById<ImageButton>(R.id.btnFooterPedidos)
        val btnInicio = activity.findViewById<ImageButton>(R.id.btnFooterInicio)
        val btnPerfil = activity.findViewById<ImageButton>(R.id.btnFooterPerfil)

        btnPedidos.setOnClickListener { v: View? ->
            activity.startActivity(
                Intent(
                    activity,
                    CompradorSolicitudes::class.java
                )
            )
        }

        btnInicio.setOnClickListener { v: View? ->
            activity.startActivity(
                Intent(
                    activity,
                    CompradorInicio::class.java
                )
            )
        }

        btnPerfil.setOnClickListener { v: View? ->
            activity.startActivity(
                Intent(
                    activity,
                    CompradorPerfil::class.java
                )
            )
        }
    }

    fun setupRepartidorFooter(activity: Activity){
        val btnPedidos = activity.findViewById<ImageButton>(R.id.btnFooterPedidos)
        val btnInicio = activity.findViewById<ImageButton>(R.id.btnFooterInicio)
        val btnPerfil = activity.findViewById<ImageButton>(R.id.btnFooterPerfil)

        btnPedidos.setOnClickListener { v: View? ->
            activity.startActivity(
                Intent(
                    activity,
                    RepartidorHistorial::class.java
                )
            )
        }

        btnInicio.setOnClickListener { v: View? ->
            activity.startActivity(
                Intent(
                    activity,
                    RepartidorInicio::class.java
                )
            )
        }

        btnPerfil.setOnClickListener { v: View? ->
            activity.startActivity(
                Intent(
                    activity,
                    RepartidorPerfil::class.java
                )
            )
        }
    }

    fun setupHeader(activity: Activity, title: String?) {
        val titulo = activity.findViewById<TextView>(R.id.tvHeaderTitulo)
        titulo.text = title
    }
}