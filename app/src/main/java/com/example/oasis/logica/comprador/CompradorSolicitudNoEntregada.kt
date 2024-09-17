package com.example.oasis.logica.comprador

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.oasis.R
import com.example.oasis.datos.Data
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Solicitud

class CompradorSolicitudNoEntregada : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_solicitud_no_entregada)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Solicitud en camino")
        requestPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita permiso de ubicación para continuar", Data.MY_PERMISSIONS_REQUEST_LOCATION)
    }

    private fun initUI(){
        val solicitud = intent.getSerializableExtra("solicitud") as Solicitud
        initResumen(solicitud)
    }

    private fun initResumen(solicitud: Solicitud){
        val solicitudTotal = findViewById<TextView>(R.id.tvSolicitudCostoTotal)
        val estado = findViewById<TextView>(R.id.tvSolicitudEstado)
        val solicitudDireccion = findViewById<TextView>(R.id.tvSolicitudDireccion)

        solicitudTotal.text = solicitud.getTotal().toString()
        estado.text = solicitud.getEstado()
        solicitudDireccion.text = solicitud.getDireccion()
    }

    private fun requestPermissions(context: Activity, permiso: String, justificacion:String, idCode:Int){
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            initUI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Data.MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initUI()
                } else {
                    denegarFuncionalidad()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun denegarFuncionalidad() {
        val tvSolicitudEstadoRepartidor = findViewById<TextView>(R.id.tvSolicitudEstadoRepartidor)
        val ivSolicitudNooEntregada = findViewById<ImageView>(R.id.ivSolicitudNoEntregada)

        tvSolicitudEstadoRepartidor.text = "Sin acceso a ubicación"
        ivSolicitudNooEntregada.setImageResource(R.drawable.warning)
    }
}