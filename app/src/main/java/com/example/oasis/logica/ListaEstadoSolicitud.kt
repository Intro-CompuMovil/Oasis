package com.example.oasis.logica

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.oasis.R
import com.example.oasis.model.Order
import com.example.oasis.model.Solicitud

class ListaEstadoSolicitud (private val solicitud: Solicitud) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.activity_estado_solicitud, null)

        val lvOrders = view.findViewById<ListView>(R.id.lvOrders)
        lvOrders.adapter = OrderAdapter(solicitud.getOrdenes())

        builder.setView(view)
        return builder.create()
    }

    private inner class OrderAdapter(private val orders: List<Order>) : ArrayAdapter<Order>(requireContext(), 0, orders) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.estado_solicitud_item, parent, false)
            val order = getItem(position)

            val ivProductImage = view.findViewById<ImageView>(R.id.ivProductImage)
            val tvProductName = view.findViewById<TextView>(R.id.tvProductName)
            val tvQuantity = view.findViewById<TextView>(R.id.tvQuantity)
            val tvStatus = view.findViewById<TextView>(R.id.tvStatus)

            order?.let {
                Glide.with(context).load(it.getProducto().getImagen()).into(ivProductImage)
                tvProductName.text = it.getProducto().getNombre()
                tvQuantity.text = "Cantidad: ${it.getCantidad()}"
                tvStatus.text = it.getEstadoOrden()
                tvStatus.setTextColor(
                    ContextCompat.getColor(context, if (it.getEstadoOrden() == "Recogido") R.color.verde else R.color.rojo)
                )
            }

            return view
        }
    }
}