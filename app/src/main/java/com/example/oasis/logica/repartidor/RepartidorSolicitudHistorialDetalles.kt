package com.example.oasis.logica.repartidor

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.adapters.RepartidorSolicitudDetallesAdapter
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Solicitud

class RepartidorSolicitudHistorialDetalles : AppCompatActivity() {
    private lateinit var solicitud: Solicitud
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor_solicitud_historial_detalles)

        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Detalles de la solicitud")
        initUI()
    }

    private fun initUI(){
        solicitud = intent.getSerializableExtra("solicitud") as Solicitud
        val tvDistanciaFinal = findViewById<TextView>(R.id.tvRepartidorSolicitudDistanciaFinalProductos)
        val tvComision = findViewById<TextView>(R.id.tvRepartidorSolicitudComision)

        tvDistanciaFinal.text = "8.5Km"
        tvComision.text = solicitud.getTotal().toString()

        initRecycleView()
    }

    private fun initRecycleView(){
        val recyclerView = findViewById<RecyclerView>(R.id.rvRepartidorSolicitudProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RepartidorSolicitudDetallesAdapter(this, solicitud.getOrdenes())
    }
}