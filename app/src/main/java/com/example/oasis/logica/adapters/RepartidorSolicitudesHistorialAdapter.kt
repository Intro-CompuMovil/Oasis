package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.repartidor.RepartidorSolicitudHistorialDetalles
import com.example.oasis.logica.utility.DateHelper
import com.example.oasis.model.Solicitud

class RepartidorSolicitudesHistorialAdapter (private val context: Context, private val solicitudes: List<Solicitud>) : RecyclerView.Adapter<RepartidorSolicitudesHistorialAdapter.RepartidorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepartidorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.repartidor_solicitud_historial_items, parent, false)
        return RepartidorViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepartidorViewHolder, position: Int) {
        val solicitud = solicitudes[position]
        holder.fecha.text = DateHelper().getDateWithHour(solicitud.getFechaAsLocalDateTime())
        holder.comision.text = (solicitud.getTotal() * 0.1).toString()
        holder.direccion.text = solicitud.getUbicacion().getDireccion()
        holder.numProductos.text = solicitud.getOrdenes().size.toString()

        holder.itemView.setOnClickListener {
            Intent(context, RepartidorSolicitudHistorialDetalles::class.java).apply {
                putExtra("solicitud", solicitud)
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return solicitudes.size
    }

    inner class RepartidorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fecha: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudFecha)
        val comision: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudComision)
        val direccion: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudDireccion)
        val numProductos: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudNumProductos)
    }
}