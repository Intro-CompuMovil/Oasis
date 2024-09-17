package com.example.oasis.logica.comprador

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.adapters.CompradorSolicitudAdapter
import com.example.oasis.logica.adapters.CompradorSolicitudesAdapter
import com.example.oasis.logica.utility.UIHelper

class CompradorSolicitudes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_solicitudes)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Mis Solicitudes")
        initUI()
    }

    private fun initUI(){
        initSolicitudes()
    }

    private fun initSolicitudes(){
        val rvSolicitudes = findViewById<RecyclerView>(R.id.rvCompradorSolicitudes)

        val solicitudes = MainActivity.solicitudesList
        rvSolicitudes.layoutManager = LinearLayoutManager(this)
        rvSolicitudes.adapter = CompradorSolicitudesAdapter(this, solicitudes)
    }
}