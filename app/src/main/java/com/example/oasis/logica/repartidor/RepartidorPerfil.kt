package com.example.oasis.logica.repartidor

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.utility.UIHelper

class RepartidorPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor_perfil)
        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Perfil")
        initUI()
        initSalir()
    }

    private fun initUI(){
        val tvNombre = findViewById<TextView>(R.id.tvNombrePerfil)
        val tvCorreo = findViewById<TextView>(R.id.tvCorreoPerfil)

        tvNombre.text = MainActivity.repartidorNombre
        tvCorreo.text = "repartidor1@example.com"
    }

    private fun initSalir(){
        val btnSalir = findViewById<TextView>(R.id.btnSalir)

        btnSalir.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}