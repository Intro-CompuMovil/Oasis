package com.example.oasis.logica.comprador

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.adapters.CompradorSolicitudAdapter
import com.example.oasis.logica.utility.DateHelper
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Solicitud

class CompradorDetallesSolicitud : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_detalles_solicitud)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Detalles de la solicitud")
        initUI()
    }

    private fun initUI(){
        val solicitud = intent.getSerializableExtra("solicitud") as Solicitud
        initResumen(solicitud)
        initRecyleView(solicitud)
    }

    private fun initResumen(solicitud: Solicitud){
        val carritoTotal = findViewById<TextView>(R.id.tvCompradorSolicitudTotal)
        val subtotalProductos = findViewById<TextView>(R.id.tvCompradorSolicitudSubtotalProductos)
        val otrosCobros = findViewById<TextView>(R.id.tvCompradorSolicitudCobros)
        val fecha = findViewById<TextView>(R.id.tvCompradorSolicitudFecha)
        val estado = findViewById<TextView>(R.id.tvCompradorSolicitudEstado)

        val subtotalProductosResultado = getProductosSubtotal(solicitud)
        val comision = subtotalProductosResultado * 0.1
        subtotalProductos.text = subtotalProductosResultado.toString()
        otrosCobros.text = comision.toString()
        carritoTotal.text = solicitud.getTotal().toString()
        fecha.text = DateHelper().getDateWithHour(solicitud.getFecha())
        estado.text = solicitud.getEstado()
    }

    private fun initRecyleView(solicitud: Solicitud){
        val recyclerView = findViewById<RecyclerView>(R.id.rvCompradorSolicitudProductos)
        val adapter = CompradorSolicitudAdapter(this, solicitud.getOrdenes())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getProductosSubtotal(solicitud: Solicitud): Double{
        val ordenes = solicitud.getOrdenes()
        var subtotal = 0.0
        for(orden in ordenes){
            subtotal += orden.getProducto().getPrecio() * orden.getCantidad()
        }
        return subtotal
    }
}