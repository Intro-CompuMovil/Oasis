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
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.repartidor.RepartidorInicio
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.FieldValidatorHelper
import com.example.oasis.model.Order
import com.example.oasis.model.Product
import com.example.oasis.model.Solicitud

class MainActivity : AppCompatActivity() {
    companion object{
        private var carrito: MutableList<Order> = mutableListOf()
        private var productsList: MutableList<Product> = mutableListOf()

        var solicitudesList: MutableList<Solicitud> = mutableListOf()

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

    private lateinit var emailError: TextView
    private lateinit var passwordError: TextView
    private lateinit var dataBaseSimulator: DataBaseSimulator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataBaseSimulator = DataBaseSimulator(this)
        initUI()
    }

    private fun initUI(){
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)
        initIniciarSesion(email, password)
        initRegistrarse()
    }

    private fun initIniciarSesion(email: EditText, password: EditText){
        val btnIniciarSesion = findViewById<Button>(R.id.launcherIniciarSesion)
        val radioButtonComprador = findViewById<RadioButton>(R.id.radioButtonComprador)

        btnIniciarSesion.setOnClickListener {
            val emailTxt = email.text.toString()
            val passwordTxt = password.text.toString()
            if (!checkErrors(emailTxt, passwordTxt)){
                if (radioButtonComprador.isChecked){
                    iniciarSesionComprador(emailTxt, passwordTxt)
                }
                else{
                    iniciarSesionRepartidor(emailTxt, passwordTxt)
                }
            }
        }
    }

    private fun iniciarSesionComprador(email: String, password: String){
        val comprador = dataBaseSimulator.loginComprador(email, password)
        if (comprador != null){
            CompradorInicio.comprador = comprador
            val intent = Intent(this, CompradorInicio::class.java)
            startActivity(intent)
        }else{
            AppUtilityHelper.showErrorDialog(this,  "Usuario o contraseña incorrectos")
        }
    }

    private fun iniciarSesionRepartidor(email: String, password: String){
        val repartidor = dataBaseSimulator.loginRepartidor(email, password)
        if (repartidor != null){
            RepartidorInicio.repartidor = repartidor
            val intent = Intent(this, RepartidorInicio::class.java)
            startActivity(intent)
        } else{
            AppUtilityHelper.showErrorDialog(this,  "Usuario o contraseña incorrectos")
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

    private fun checkErrors(email: String, password: String): Boolean{
        var error = false
        if (email.isEmpty()){
            emailError.text = "Campo vacío"
            error = true
        } else{emailError.text = ""}

        if (!FieldValidatorHelper().validateEmail(email)){
            emailError.text = "Email inválido"
            error = true
        }else{emailError.text = ""}

        if (password.isEmpty()){
            passwordError.text = "Campo vacío"
            error = true
        }else{passwordError.text = ""}

        return error
    }
}