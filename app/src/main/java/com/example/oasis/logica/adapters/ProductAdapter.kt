package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.comprador.CompradorInicio
import com.example.oasis.logica.comprador.DetallesProducto
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.model.Order
import com.example.oasis.model.Product
import kotlin.math.min


class ProductAdapter(private val context: Context,
                     private val productList: List<Product>,
                     private val compradorInicio: CompradorInicio
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.explorar_categoria_items, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.getNombre()
        AppUtilityHelper.cargarProductoImagen(holder.btnProduct, product, context)
        // holder.imgProduct.setImageResource(product.image)

        holder.btnProduct.setOnClickListener {
            Intent(context, DetallesProducto::class.java).apply {
                putExtra("product", product)
                context.startActivity(this)
            }
        }
        holder.btnAddProduct.setOnClickListener {
            if (MainActivity.addProductToCarrito(Order(product, 1, "No entregado"))) {
                Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
                compradorInicio.initNumProductosCarrito()
                holder.btnAddProduct.setImageResource(R.drawable.check)
                holder.btnAddProduct.setBackgroundResource(R.drawable.button_product_added)
            } else {
                Toast.makeText(context, "Producto ya está en el carrito, cantidad actualizada", Toast.LENGTH_SHORT).show()
            }
        }
        checkProductInCarrito(product, holder)
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

    private fun checkProductInCarrito(product: Product, holder: ProductViewHolder) {
        if (MainActivity.productIsInCarrito(product.getId())) {
            holder.btnAddProduct.setImageResource(R.drawable.check)
            holder.btnAddProduct.setBackgroundResource(R.drawable.button_product_added)
        }
    }
}