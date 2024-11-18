package com.example.oasis.logica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.comprador.CompradorInicio
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.db.FireBaseDataBase
import com.example.oasis.logica.repartidor.RepartidorInicio
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.FieldValidatorHelper
import com.example.oasis.model.Comprador
import com.example.oasis.model.Repartidor
import kotlinx.coroutines.launch

class Registrarse : AppCompatActivity() {
    private lateinit var emailError: TextView
    private lateinit var passwordError: TextView
    private lateinit var nombreError: TextView
    private lateinit var dataBaseSimulator: DataBaseSimulator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        dataBaseSimulator = DataBaseSimulator(this)
        initUI()
    }

    private fun initUI(){
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val nombre = findViewById<EditText>(R.id.nombreEditText)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)
        nombreError = findViewById(R.id.nombreError)
        initIniciarSesion(email, password, nombre)
        initRegistrarse()
    }

    private fun initIniciarSesion(email: EditText, password: EditText, nombre: EditText){
        val btnIniciarSesion = findViewById<Button>(R.id.launcherIniciarSesion)
        val radioButtonComprador = findViewById<RadioButton>(R.id.radioButtonComprador)
        btnIniciarSesion.setOnClickListener {
            val emailTxt = email.text.toString()
            val passwordTxt = password.text.toString()
            val nombreTxt = nombre.text.toString()
            if (!checkErrors(emailTxt, passwordTxt, nombreTxt)){
                if (radioButtonComprador.isChecked){
                    registrarComprador(emailTxt, passwordTxt, nombreTxt)
                }
                else{
                    registrarRepartidor(emailTxt, passwordTxt, nombreTxt)
                }
            }
        }
    }

    private fun registrarComprador(email: String, password: String, nombre: String){
        val comprador = Comprador("", nombre, email, password, "",mutableListOf())
        lifecycleScope.launch {
            val dataBase = FireBaseDataBase()
            dataBase.registerUser(comprador.getEmail(), comprador.getContrasena())
            dataBase.createComprador(comprador)

            if (comprador.getId().isNotEmpty()) {
                CompradorInicio.comprador = comprador
                val intent = Intent(this@Registrarse, CompradorInicio::class.java)
                startActivity(intent)
            } else{
                AppUtilityHelper.showErrorDialog(this@Registrarse,  "El email ya está registrado")
            }
        }
    }

    private fun registrarRepartidor(email: String, password: String, nombre: String){
        val repartidor = Repartidor("1", nombre, email, password, "")
        lifecycleScope.launch {
            val dataBase = FireBaseDataBase()
            dataBase.registerUser(repartidor.getEmail(), repartidor.getContrasena())
            dataBase.createRepartidor(repartidor)

            if (repartidor.getId().isNotEmpty()) {
                RepartidorInicio.repartidor = repartidor
                val intent = Intent(this@Registrarse, RepartidorInicio::class.java)
                startActivity(intent)
            } else{
                AppUtilityHelper.showErrorDialog(this@Registrarse,  "El email ya está registrado")
            }
        }
    }
    private fun initRegistrarse(){
        val btnIniciarSesion = findViewById<TextView>(R.id.launcherIrIniciarSesion)
        btnIniciarSesion.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun checkErrors(email: String, password: String, nombre:String): Boolean{
        var error = false
        if (email.isEmpty()){
            emailError.text = "Campo vacío"
            error = true
        }else{emailError.text = ""}

        if (!FieldValidatorHelper().validateEmail(email)){
            emailError.text = "Email inválido"
            error = true
        }else{emailError.text = ""}

        if (password.isEmpty()){
            passwordError.text = "Campo vacío"
            error = true
        }else{passwordError.text = ""}

        if (nombre.isEmpty()){
            nombreError.text = "Campo vacío"
            error = true
        }else{nombreError.text = ""}

        return error
    }
}