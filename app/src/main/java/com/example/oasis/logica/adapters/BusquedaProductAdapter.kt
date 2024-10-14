package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.comprador.DetallesProducto
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.model.Product

class BusquedaProductAdapter(private val context: Context, private val productList: List<Product>) :
    RecyclerView.Adapter<BusquedaProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.busqueda_producto_items, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productNombre.text = product.getNombre()
        holder.productCalificacion.text = product.getPuntaje().toString()+"/5"
        holder.productPrecio.text = "$"+product.getPrecio().toString()
        AppUtilityHelper.cargarProductoImagen(holder.productImage, product, context)

        // holder.imgProduct.setImageResource(product.image)
        holder.itemView.setOnClickListener {
            Intent(context, DetallesProducto::class.java).apply {
                putExtra("product", product)
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.ivBusquedaProductoImagen)
        val productNombre: TextView = itemView.findViewById(R.id.tvBusquedaProductoNombre)
        val productCalificacion: TextView = itemView.findViewById(R.id.tvBusquedaProductoCalificacion)
        val productPrecio: TextView = itemView.findViewById(R.id.tvBusquedaProductoPrecio)
    }
}