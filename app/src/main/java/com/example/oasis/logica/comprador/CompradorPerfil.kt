package com.example.oasis.logica.comprador

import DireccionesAdapter
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

class CompradorPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_perfil)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Perfil")
        initUI()
        initSalir()
    }

    private fun initUI(){
        val tvNombre = findViewById<TextView>(R.id.tvNombrePerfil)
        val tvCorreo = findViewById<TextView>(R.id.tvCorreoPerfil)
        val lvDirecciones = findViewById<ListView>(R.id.lvDireccionesPerfil)

        tvNombre.text = MainActivity.getUsuarioNombre()
        tvCorreo.text = "user1@example.com"
        lvDirecciones.adapter = DireccionesAdapter(this, MainActivity.direccionesList)
        lvDirecciones.isClickable = false
        lvDirecciones.isEnabled = false
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