package com.example.oasis.logica.comprador

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
        initNumProductosCarrito()
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

    private fun initRecyclerView(){
        val categoryList = getCategories()
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val categoryAdapter = CategoryAdapter(this, categoryList, this)
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = categoryAdapter
    }

    fun initNumProductosCarrito(){
        val cartItemCount = findViewById<TextView>(R.id.cartItemCount)
        val numProductos = MainActivity.getCarrito().size

        if (numProductos == 0){
            cartItemCount.isVisible = false
        }
        else if (numProductos > 9){
            cartItemCount.text = "9+"
            cartItemCount.isVisible = true
        }
        else{
            cartItemCount.text = numProductos.toString()
            cartItemCount.isVisible = true
        }
    }

    private fun getCategories(): List<Category> {
        /*val productsList = mutableListOf<Product>(
            Product(1, "Laptop", "Laptop HP", 4.7f, 1000.0, "Tecnología"),
            Product(2, "Smartphone", "Smartphone Samsung", 4.5f, 500.0, "Tecnología"),
            Product(3, "Tablet", "Tablet Lenovo", 4.3f, 300.0, "Tecnología"),
            Product(4, "Smartwatch", "Smartwatch Xiaomi", 4.2f, 200.0, "Tecnología"),
            Product(5, "Audífonos", "Audífonos Sony", 4.1f, 100.0, "Tecnología"),
            Product(6, "Sofá", "Sofá de 3 plazas", 4.8f, 800.0, "Hogar"),
            Product(7, "Mesa", "Mesa de comedor", 4.6f, 400.0, "Hogar"),
            Product(8, "Silla", "Silla de oficina", 4.4f, 200.0, "Hogar"),
            Product(9, "Cama", "Cama matrimonial", 4.3f, 600.0, "Hogar"),
            Product(10, "Escritorio", "Escritorio de madera", 4.2f, 300.0, "Hogar"),
            Product(11, "Camisa", "Camisa de vestir", 4.7f, 50.0, "Moda"),
            Product(12, "Pantalón", "Pantalón de mezclilla", 4.5f, 40.0, "Moda"),
            Product(13, "Zapatos", "Zapatos de piel", 4.3f, 60.0, "Moda"),
            Product(14, "Chamarra", "Chamarra de cuero", 4.2f, 70.0, "Moda"),
            Product(15, "Reloj", "Reloj de pulsera", 4.1f, 30.0, "Moda"),
            Product(16, "Balón", "Balón de fútbol", 4.8f, 20.0, "Deportes"),
            Product(17, "Raqueta", "Raqueta de tenis", 4.6f, 30.0, "Deportes"),
            Product(18, "Tenis", "Tenis para correr", 4.4f, 40.0, "Deportes"),
            Product(19, "Bicicleta", "Bicicleta de montaña", 4.3f, 200.0, "Deportes"),
            Product(20, "Pesas", "Pesas de 5 kg", 4.2f, 50.0, "Deportes")
        )*/
        val productsList = dataBase.getProducts()
        MainActivity.setProductsList(productsList)

        val categoryList = productsList.groupBy { it.getCategoria() }.map { Category(it.key, it.value) }
        /*val solicitud = Solicitud(1, listOf( Order(MainActivity.getProductsList()[0], 1), Order(
            MainActivity.getProductsList()[5], 3)
        ), 3740.0, LocalDateTime.parse("2021-10-10T10:10:10"), "Entregado", "Calle 1 # 1-1")*/

        //if (MainActivity.solicitudesList.isEmpty()) MainActivity.solicitudesList = mutableListOf(solicitud)

        return categoryList
    }
}