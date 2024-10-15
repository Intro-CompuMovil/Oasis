package com.example.oasis.logica.repartidor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.datos.Data
import com.example.oasis.logica.adapters.RepartidorSolicitudDetallesAdapter
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Solicitud

class RepartidorSolicitudDetalles : AppCompatActivity() {
    private lateinit var btnAceptar: Button
    private lateinit var solicitud: Solicitud
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor_solicitud_detalles)

        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Detalles de la solicitud")
        initUI()
    }

    private fun initUI(){
        solicitud = intent.getSerializableExtra("solicitud") as Solicitud
        val tvComision = findViewById<TextView>(R.id.tvRepartidorSolicitudComision)
        val tvDireccion = findViewById<TextView>(R.id.tvRepartidorSolicitudDireccion)

        tvComision.text = solicitud.getTotal().toString()
        tvDireccion.text = solicitud.getUbicacion().getDireccion()

        initRecycleView()
        initAceptar()
    }

    private fun initRecycleView(){
        val recyclerView = findViewById<RecyclerView>(R.id.rvRepartidorSolicitudProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RepartidorSolicitudDetallesAdapter(this, solicitud.getOrdenes())
    }

    private fun initAceptar(){
        btnAceptar = findViewById(R.id.btnRepartidorSolicitudAceptar)

        btnAceptar.setOnClickListener {
            requestPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita permiso de ubicación para continuar", Data.MY_PERMISSIONS_REQUEST_LOCATION)
        }
    }

    private fun requestPermissions(context: Activity, permiso: String, justificacion:String, idCode:Int){
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            Intent(this, RepartidorEntrega::class.java).apply {
                putExtra("solicitud", solicitud)
                startActivity(this)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Data.MY_PERMISSIONS_REQUEST_LOCATION -> {
                //val textView = findViewById<TextView>(R.id.resultadosContactosTextView)
                var mensaje = ""
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Intent(this, RepartidorEntrega::class.java).apply {
                        putExtra("solicitud", solicitud)
                        startActivity(this)
                    }
                } else {
                    denegarFuncionalidad()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun denegarFuncionalidad(){
        btnAceptar.isEnabled = false
        btnAceptar.isClickable = false
        btnAceptar.background = ContextCompat.getDrawable(this, R.drawable.button_disabled)
        Toast.makeText(this, "No puede seguir sin dar permiso de ubicación", Toast.LENGTH_SHORT).show()
    }
}