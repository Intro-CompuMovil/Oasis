package com.example.oasis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.oasis.logica.Registrarse
import com.example.oasis.logica.comprador.CompradorInicio
import com.example.oasis.logica.repartidor.RepartidorInicio
import com.example.oasis.model.Order
import com.example.oasis.model.Product
import com.example.oasis.model.Solicitud

class MainActivity : AppCompatActivity() {
    companion object{
        private var usuarioNombre: String = "Usuario1"
        private var carrito: MutableList<Order> = mutableListOf()
        private var productsList: MutableList<Product> = mutableListOf()
        var direccionesList: MutableList<String> = mutableListOf(
            "Calle 1 # 1-1",
            "Calle 2 # 2-2",
            "Calle 3 # 3-3",
            "Calle 4 # 4-4"
        )

        var solicitudesList: MutableList<Solicitud> = mutableListOf()
        val repartidorNombre = "Repartidor1"

        fun getUsuarioNombre(): String{
            return usuarioNombre
        }
        fun setUsuarioNombre(nombre: String){
            usuarioNombre = nombre
        }
        fun getCarrito(): MutableList<Order>{
            return carrito
        }
        fun setCarrito(productList: MutableList<Order>){
            carrito = productList
        }
        fun addProductToCarrito(order: Order):Boolean{
            if (productIsInCarrito(order.getProducto().getId())){
                carrito.forEach { if (it.getProducto().getId()==order.getProducto().getId()) it.setCantidad(order.getCantidad()) }
                return false
            }
            carrito.add(order)
            return true
        }
        fun removeProductFromCarrito(order: Order){
            carrito.remove(order)
        }
        fun productIsInCarrito(id: Int): Boolean{
            return carrito.any { it.getProducto().getId()==id }
        }
        fun getProductsList(): MutableList<Product>{
            return productsList
        }
        fun setProductsList(productList: MutableList<Product>){
            productsList = productList
        }
        fun clearCarrito(){
            carrito = mutableListOf()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    private fun initUI(){
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        initIniciarSesion(email, password)
        initRegistrarse()
    }

    private fun initIniciarSesion(email: EditText, password: EditText){
        val btnIniciarSesion = findViewById<Button>(R.id.launcherIniciarSesion)
        val radioButtonComprador = findViewById<RadioButton>(R.id.radioButtonComprador)
        btnIniciarSesion.setOnClickListener {
            val emailTxt = email.text.toString()
            val passwordTxt = password.text.toString()
            if (emailTxt.isNotEmpty() && passwordTxt.isNotEmpty()){
                if (radioButtonComprador.isChecked){
                    val intent = Intent(this, CompradorInicio::class.java)
                    startActivity(intent)
                }
                else{
                    Intent(this, RepartidorInicio::class.java).apply {
                        startActivity(this)
                    }
                }
            }
        }
    }
    private fun initRegistrarse(){
        val btnRegistrarse = findViewById<TextView>(R.id.launcherRegistrate)
        btnRegistrarse.setOnClickListener {
            Intent(this, Registrarse::class.java).apply {
                startActivity(this)
            }
        }
    }
}