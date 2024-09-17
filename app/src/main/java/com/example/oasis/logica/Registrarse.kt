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

class Registrarse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

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
        val btnIniciarSesion = findViewById<TextView>(R.id.launcherIrIniciarSesion)
        btnIniciarSesion.setOnClickListener {
            Intent(this, MainActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}