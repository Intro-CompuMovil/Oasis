package com.example.oasis.logica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.comprador.CompradorInicio
import com.example.oasis.logica.repartidor.RepartidorInicio
import com.example.oasis.logica.utility.FieldValidatorHelper

class Registrarse : AppCompatActivity() {
    private lateinit var emailError: TextView
    private lateinit var passwordError: TextView
    private lateinit var nombreError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

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