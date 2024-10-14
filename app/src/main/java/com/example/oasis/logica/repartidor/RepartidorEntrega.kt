package com.example.oasis.logica.repartidor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.datos.Data
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Solicitud

class RepartidorEntrega : AppCompatActivity() {
    private var fotoTomada = false
    private lateinit var btnFoto: ImageButton
    private lateinit var btnFinalizar: Button

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val extras: Bundle? = data?.extras
            val imageBitmap = extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                btnFoto.setImageBitmap(imageBitmap)
                fotoTomada = true
            } else {
                Toast.makeText(this, "Error al capturar la imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Captura de imagen cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor_entrega)

        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Ruta de entrega")
        requestLocationPermission(this)
    }

    private fun initUI(){
        val solicitud = intent.getSerializableExtra("solicitud") as Solicitud
        initResumen(solicitud)
        initButtons()
        initFinalizar(solicitud)
    }

    private fun initResumen(solicitud: Solicitud){
        val distanciaTotal = findViewById<TextView>(R.id.tvSolicitudDistanciaTotal)
        val comision = findViewById<TextView>(R.id.tvSolicitudComision)
        val solicitudDireccion = findViewById<TextView>(R.id.tvSolicitudDireccion)

        distanciaTotal.text = "8.5"
        comision.text = solicitud.getTotal().times(0.1).toString()
        solicitudDireccion.text = solicitud.getUbicacion().getDireccion()
    }

    private fun initButtons(){
        btnFoto = findViewById(R.id.ibSolicitudNoEntregada)

        btnFoto.setOnClickListener {
            requestCameraPermission(this)
        }

    }

    private fun initFinalizar(solicitud: Solicitud){
        btnFinalizar = findViewById(R.id.btnRepartidorSolicitudFinalizar)
        val rbProductosRecogidos = findViewById<RadioButton>(R.id.rbSolicitudProductosRecogidosSi)
        btnFinalizar.setOnClickListener {
            if (fotoTomada && rbProductosRecogidos.isChecked){
                MainActivity.solicitudesList.first { it.getId() == solicitud.getId() }.setEstado("Entregado")
                Intent(this, RepartidorInicio::class.java).apply {
                    startActivity(this)
                }
            }else if (!fotoTomada){
                Toast.makeText(this, "Debe tomar una foto para finalizar", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Debe confirmar que recogió los productos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tomarFoto(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun requestLocationPermission(context: Activity){
        val permiso = android.Manifest.permission.ACCESS_FINE_LOCATION
        val idCode = Data.MY_PERMISSIONS_REQUEST_LOCATION
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            initUI()
        }
    }

    private fun requestCameraPermission(context: Activity){
        val permiso = android.Manifest.permission.CAMERA
        val idCode = Data.MY_PERMISSIONS_REQUEST_CAMERA
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            tomarFoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Data.MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initUI()
                } else {
                    Toast.makeText(this, "Necesita la localización para terminar", Toast.LENGTH_SHORT).show()
                    denegarFuncionalidad()
                }
                return
            }
            Data.MY_PERMISSIONS_REQUEST_CAMERA ->{
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    tomarFoto()
                } else {
                    Toast.makeText(this, "Necesita la cámara para finalizar", Toast.LENGTH_SHORT).show()
                    denegarFuncionalidad()
                }
            }
            else -> {

            }
        }
    }

    private fun denegarFuncionalidad() {
        btnFinalizar.isEnabled = false
        btnFinalizar.isClickable = false
        btnFinalizar.background = ContextCompat.getDrawable(this, R.drawable.button_disabled)
    }
}