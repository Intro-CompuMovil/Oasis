package com.example.oasis.logica.repartidor

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.logica.adapters.RepartidorSolicitudesHistorialAdapter
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.db.FireBaseDataBase
import com.example.oasis.logica.utility.UIHelper
import kotlinx.coroutines.launch

class RepartidorHistorial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor_historial)

        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Historial de Entregas")
        initUI()
    }

    private fun initUI(){
        initSolicitudes()
    }

    private fun initSolicitudes(){
        val rvSolicitudes = findViewById<RecyclerView>(R.id.rvRepartidorSolicitudesHistorial)

        lifecycleScope.launch {
            val solicitudes = FireBaseDataBase().getSolicitudesByUserID(RepartidorInicio.repartidor.getId())
            rvSolicitudes.layoutManager = LinearLayoutManager(this@RepartidorHistorial)
            rvSolicitudes.adapter = RepartidorSolicitudesHistorialAdapter(this@RepartidorHistorial, solicitudes)
        }

    }
}