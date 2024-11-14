package com.example.oasis.logica.comprador

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.adapters.CompradorSolicitudAdapter
import com.example.oasis.logica.adapters.CompradorSolicitudesAdapter
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.db.FireBaseDataBase
import com.example.oasis.logica.utility.UIHelper
import kotlinx.coroutines.launch

class CompradorSolicitudes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_solicitudes)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Mis Solicitudes")
        initUI()
    }

    private fun initUI(){
        initSolicitudes()
    }

    private fun initSolicitudes(){
        val rvSolicitudes = findViewById<RecyclerView>(R.id.rvCompradorSolicitudes)

        val database = FireBaseDataBase()

        lifecycleScope.launch {
            val solicitudes = database.getSolicitudesByUserID(CompradorInicio.comprador.getId())
            if (solicitudes.isNotEmpty()){
                rvSolicitudes.layoutManager = LinearLayoutManager(this@CompradorSolicitudes)
                rvSolicitudes.adapter = CompradorSolicitudesAdapter(this@CompradorSolicitudes, solicitudes)
            }else{
                Toast.makeText(this@CompradorSolicitudes, "No hay solicitudes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}