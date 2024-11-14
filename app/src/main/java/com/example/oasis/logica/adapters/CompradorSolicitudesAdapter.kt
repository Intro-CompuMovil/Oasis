package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.comprador.CompradorDetallesSolicitud
import com.example.oasis.logica.comprador.CompradorSolicitudNoEntregada
import com.example.oasis.logica.utility.DateHelper
import com.example.oasis.model.Solicitud

class CompradorSolicitudesAdapter(private val context: Context, private val solicitudesList: List<Solicitud>) :
    RecyclerView.Adapter<CompradorSolicitudesAdapter.CompradorSolicitudesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompradorSolicitudesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comprador_solicitud_items, parent, false)
        return CompradorSolicitudesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompradorSolicitudesViewHolder, position: Int) {
        val request = solicitudesList[position]
        holder.tvSolicitudEstado.text = request.getEstado()
        holder.tvSolicitudTotal.text = request.getTotal().toString()
        holder.tvCantidadProductos.text = request.getOrdenes().size.toString()
        holder.tvSolicitudFecha.text = DateHelper().getDateWithHour(request.getFechaAsLocalDateTime())

        holder.itemView.setOnClickListener {
            if (request.getEstado() == "Entregado") {
                val intent = Intent(context, CompradorDetallesSolicitud::class.java)
                intent.putExtra("solicitud", request)
                context.startActivity(intent)
            }else{
                Intent(context, CompradorSolicitudNoEntregada::class.java).apply {
                    putExtra("solicitud", request)
                    context.startActivity(this)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return solicitudesList.size
    }

    class CompradorSolicitudesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSolicitudFecha: TextView = itemView.findViewById(R.id.tvCompradorSolicitudFecha)
        val tvSolicitudEstado: TextView = itemView.findViewById(R.id.tvCompradorSolicitudEstado)
        val tvSolicitudTotal: TextView = itemView.findViewById(R.id.tvCompradorSolicitudCosto)
        val tvCantidadProductos = itemView.findViewById<TextView>(R.id.tvCompradorSolicitudNumProductos)
    }
}