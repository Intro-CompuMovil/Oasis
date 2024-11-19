package com.example.oasis.logica.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.logica.comprador.BusquedaProductos
import com.example.oasis.logica.comprador.CompradorInicio
import com.example.oasis.model.Category

class CategoryAdapter(private val context: Context,
                      private val categoryList: List<Category>,
                        private val compradorInicio: CompradorInicio
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.explorar_categoria_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.tvCategoryName.text = category.nombre

        // Configurar el RecyclerView horizontal para los productos
        val productAdapter = ProductAdapter(context, category.listaProductos, compradorInicio)
        holder.rvProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvProducts.adapter = productAdapter

        // Lógica para el botón de "Ver más"
        holder.tvSeeMore.setOnClickListener {
            val intent = Intent(context, BusquedaProductos::class.java)
            intent.putExtra("busqueda", category.nombre)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        val rvProducts: RecyclerView = itemView.findViewById(R.id.rvProducts)
        val tvSeeMore: TextView = itemView.findViewById(R.id.tvSeeMore)
    }
}
