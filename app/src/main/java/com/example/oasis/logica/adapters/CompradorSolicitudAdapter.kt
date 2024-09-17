package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.comprador.DetallesProducto
import com.example.oasis.model.Order

    class CompradorSolicitudAdapter(private val context: Context, private val productosList: List<Order>) :
    RecyclerView.Adapter<CompradorSolicitudAdapter.CompradorSolicitudViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompradorSolicitudViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comprador_solicitud_ordenes_items, parent, false)
        return CompradorSolicitudViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompradorSolicitudViewHolder, position: Int) {
        val request = productosList[position]
        val precioTotal = request.getProducto().getPrecio() * request.getCantidad()
        holder.tvProductoNombre.text = request.getProducto().getNombre()
        holder.tvProductoCantidad.text = request.getCantidad().toString()
        holder.tvProductoTotal.text = precioTotal.toString()

        holder.itemView.setOnClickListener {
            Intent(context, DetallesProducto::class.java).apply {
                putExtra("product", request.getProducto())
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return productosList.size
    }

    class CompradorSolicitudViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductoNombre: TextView = itemView.findViewById(R.id.tvCompradorSolicitudOrdenesProductoNombre)
        val tvProductoCantidad: TextView = itemView.findViewById(R.id.tvCompradorSolicitudOrdenesProductoCantidad)
        val tvProductoTotal: TextView = itemView.findViewById(R.id.tvCompradorSolicitudOrdenesProductoPrecioTotal)
    }
}