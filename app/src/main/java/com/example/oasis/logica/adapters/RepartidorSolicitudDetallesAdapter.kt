package com.example.oasis.logica.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.model.Order

class RepartidorSolicitudDetallesAdapter (
    private val context: Context,
    private val ordenes: List<Order>
) : RecyclerView.Adapter<RepartidorSolicitudDetallesAdapter.RepartidorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepartidorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.repartidor_solicitud_detalles, parent, false)
        return RepartidorViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepartidorViewHolder, position: Int) {
        val orden = ordenes[position]
        holder.nombre.text = orden.getProducto().getNombre()
        holder.cantidad.text = orden.getCantidad().toString()
        holder.distancia.text = "2Km"
    }

    override fun getItemCount(): Int {
        return ordenes.size
    }

    inner class RepartidorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudOrdenesProductoNombre)
        val cantidad: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudOrdenesProductoCantidad)
        val distancia: TextView = itemView.findViewById(R.id.tvRepartidorSolicitudOrdenesProductoDistancia)

    }
}