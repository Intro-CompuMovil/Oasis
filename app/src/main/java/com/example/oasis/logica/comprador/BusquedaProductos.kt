package com.example.oasis.logica.comprador

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.adapters.BusquedaProductAdapter
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Product

class BusquedaProductos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda_productos)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Resultados de busqueda")
        initUI()
    }

    private fun initUI(){
        val rvProductos = findViewById<RecyclerView>(R.id.rvBusquedaProductos)

        val productos = filtrarProductos()
        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = BusquedaProductAdapter(this, productos)
    }

    private fun filtrarProductos(): List<Product>{
        val busqueda = intent.getStringExtra("busqueda").toString().lowercase()
        val productos = MainActivity.getProductsList()
        if (busqueda.isEmpty()) return productos

        val productosFiltrados = productos.filter { it.getNombre().lowercase().contains(busqueda.toString()) ||
                it.getDescripcion().lowercase().contains(busqueda.toString()) ||
                it.getCategoria().lowercase().contains(busqueda.toString()) }

        return productosFiltrados
    }
}