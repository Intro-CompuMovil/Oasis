package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.repartidor.RepartidorEntrega
import com.example.oasis.logica.repartidor.RepartidorSolicitudDetalles
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.DateHelper
import com.example.oasis.model.Solicitud
import com.example.oasis.model.Ubicacion

class RepartidorInicioAdapter(
    private val context: Context,
    private val solicitudes: List<Solicitud>,
    private val repartidorUbicacion: Ubicacion?
) : RecyclerView.Adapter<RepartidorInicioAdapter.RepartidorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepartidorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.repartidor_solicitud_cercana_items, parent, false)
        return RepartidorViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepartidorViewHolder, position: Int) {
        val solicitud = solicitudes[position]
        holder.distanciaTotal.text = getDistancia(solicitud)
        holder.comision.text = (solicitud.getTotal() * 0.1).toString()
        holder.direccion.text = solicitud.getUbicacion().getDireccion()
        holder.numProductos.text = solicitud.getOrdenes().size.toString()
        holder.fecha.text = DateHelper().getDateWithHour(solicitud.getFecha())

        holder.itemView.setOnClickListener {
            Intent(context, RepartidorSolicitudDetalles::class.java).apply {
                putExtra("solicitud", solicitud)
                context.startActivity(this)
            }
        }
    }

    private fun getDistancia(solicitud: Solicitud): String{
        if (repartidorUbicacion != null){
            val distancia = AppUtilityHelper.distanceBetweenTwoPoints(
                repartidorUbicacion.getLatitud(),
                repartidorUbicacion.getLongitud(),
                solicitud.getUbicacion().getLatitud(),
                solicitud.getUbicacion().getLongitud()
            )
            return String.format("%.2f", distancia)
        }else{
            return "?"
        }
    }

    override fun getItemCount(): Int {
        return solicitudes.size
    }

    inner class RepartidorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val distanciaTotal: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudDistancia)
        val comision: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudComision)
        val direccion: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudDireccion)
        val numProductos: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudNumProductos)
        val fecha = itemView.findViewById<TextView>(R.id.tvRepartidorSolicitudFecha)
    }
}