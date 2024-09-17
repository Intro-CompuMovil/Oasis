package com.example.oasis.logica.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.comprador.DetallesProducto
import com.example.oasis.model.Order

class CarritoAdapter(private val context: Context, private val carritoList: List<Order>) :
    RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.mi_carrito_ordenes_items, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val order = carritoList[position]
        val precioTotal = order.getProducto().getPrecio() * order.getCantidad()
        holder.tvProductName.text = order.getProducto().getNombre()
        holder.tvProductQuantity.text = order.getCantidad().toString()
        holder.tvProductPrice.text = precioTotal.toString()

        holder.tvProductName.setOnClickListener {
            Intent(context, DetallesProducto::class.java).apply {
                putExtra("product", order.getProducto())
                context.startActivity(this)
            }
        }

        // Lógica para el botón de "Eliminar"
        holder.btnEliminar.setOnClickListener {
            MainActivity.removeProductFromCarrito(order)
            (context as Activity).recreate()
        }
    }

    override fun getItemCount(): Int {
        return carritoList.size
    }

    class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvMiCarritoOrdenesProductoNombre)
        val tvProductQuantity: TextView = itemView.findViewById(R.id.tvMiCarritoOrdenesProductoCantidad)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvMiCarritoOrdenesProductoPrecioTotal)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.ibMiCarritoOrdenesProductoRemover)
    }
}