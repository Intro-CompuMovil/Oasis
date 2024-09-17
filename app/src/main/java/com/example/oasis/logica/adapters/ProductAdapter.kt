package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.comprador.DetallesProducto
import com.example.oasis.model.Order
import com.example.oasis.model.Product
import kotlin.math.min


class ProductAdapter(private val context: Context, private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.explorar_categoria_items, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.getNombre()
        // holder.imgProduct.setImageResource(product.image)
        holder.btnProduct.setOnClickListener {
            Intent(context, DetallesProducto::class.java).apply {
                putExtra("product", product)
                context.startActivity(this)
            }
        }
        holder.btnAddProduct.setOnClickListener {
            if (MainActivity.addProductToCarrito(Order(product, 1))) {
                Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Producto ya está en el carrito, cantidad actualizada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        val limit: Int = 10
        return min(limit, productList.size)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnProduct: ImageButton = itemView.findViewById(R.id.btnProduct)
        val btnAddProduct: ImageButton = itemView.findViewById(R.id.btnAddProduct)
        val productName: TextView = itemView.findViewById(R.id.explorarProductName)
    }
}