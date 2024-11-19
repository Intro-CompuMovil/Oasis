package com.example.oasis.logica.comprador

import Producto
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.adapters.CategoryAdapter
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.db.FireBaseDataBase
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Category
import com.example.oasis.model.Comprador
import com.example.oasis.model.Order
import com.example.oasis.model.Product
import com.example.oasis.model.Solicitud
import com.example.oasis.model.Ubicacion
import java.time.LocalDateTime


class CompradorInicio : AppCompatActivity() {
    private val dataBase = DataBaseSimulator(this)
    companion object{
        var comprador : Comprador = Comprador("", "", "", "","", mutableListOf())

        fun agregarDireccion(direccion: Ubicacion){
            comprador.agregarDireccion(direccion)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_inicio)

        UIHelper().setupFooter(this)
        val nombreUsuario = comprador.getNombre()
        UIHelper().setupHeader(this, "Hola, $nombreUsuario")
        initUI()
    }

    private fun initUI(){
        initBuscar()
        initButtons()
        initRecyclerView()
    }

    private fun initBuscar(){
        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        etBuscar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val intent = Intent(this, BusquedaProductos::class.java)
                intent.putExtra("busqueda", etBuscar.text.toString())
                startActivity(intent)
                true
            } else {
                false
            }
        }
    }

    private fun initButtons(){
        val btnCarrito = findViewById<ImageButton>(R.id.btnCarrito)
        btnCarrito.setOnClickListener {
            startActivity(Intent(this, MiCarrito::class.java))
        }
    }

    private fun initRecyclerView() {
        getCategories { categoryList ->
            val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
            val categoryAdapter = CategoryAdapter(this, categoryList)
            rvCategories.layoutManager = LinearLayoutManager(this)
            rvCategories.adapter = categoryAdapter
        }
    }

    private fun getCategories(onCategoriesLoaded: (List<Category>) -> Unit) {
        val productos = mutableListOf<Product>()

        val producto = Producto()
        producto.obtenerProductosDesdeFirebase { productoList ->
            // Una vez que tenemos los productos desde Firebase
            productos.addAll(productoList)

            // Agrupamos los productos por categoría
            val categoryList = productos.groupBy { it.getCategoria() }
                .map { Category(it.key, it.value) }

            // Llamamos al callback para devolver las categorías
            onCategoriesLoaded(categoryList)
        }
    }

}