package com.example.oasis.logica.comprador

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.datos.Data
import com.example.oasis.logica.adapters.CarritoAdapter
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Solicitud

class MiCarrito : AppCompatActivity(), SeleccionarDireccion.SeleccionarDireccionListener {
    private var direccionSeleccionada: String = ""
    private lateinit var btnPagar: Button
    private lateinit var tvDireccion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_carrito)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Mi carrito")
        initUI()
    }

    private fun initUI(){
        initResumen()
        initButton()
        initRecyleView()
        initDireccion()
    }

    private fun initResumen(){
        val carritoTotal = findViewById<TextView>(R.id.tvMiCarritoTotal)
        val subtotalProductos = findViewById<TextView>(R.id.tvMiCarritoSubtotalProductos)
        val otrosCobros = findViewById<TextView>(R.id.tvMiCarritoOtrosCobros)
        tvDireccion = findViewById(R.id.tvMiCarritoDireccion)

        val comision = getProductosSubtotal() * 0.1
        subtotalProductos.text = getProductosSubtotal().toString()
        otrosCobros.text = comision.toString()
        carritoTotal.text = String.format("%.2f", subtotalProductos.text.toString().toDouble() + comision)
    }

    private fun getProductosSubtotal(): Double{
        val ordenes = MainActivity.getCarrito()
        var subtotal = 0.0
        for(orden in ordenes){
            subtotal += orden.getProducto().getPrecio() * orden.getCantidad()
        }
        return subtotal
    }

    private fun initRecyleView(){
        val recyclerView = findViewById<RecyclerView>(R.id.rvMiCarritoProductos)
        val adapter = CarritoAdapter(this, MainActivity.getCarrito())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initButton(){
        btnPagar = findViewById<Button>(R.id.btnMiCarritoPagar)
        btnPagar.setOnClickListener {
            if (MainActivity.getCarrito().isNotEmpty()){
                requestPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "Se necesita permiso de ubicación para continuar", Data.MY_PERMISSIONS_REQUEST_LOCATION)
            }
            else{
                Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun procederPago(){
        if (direccionSeleccionada.isNotEmpty()){
            val solicitud = getSolicitud()
            MainActivity.solicitudesList.add(solicitud)
            MainActivity.clearCarrito()
            val intent = Intent(this, CompradorSolicitudNoEntregada::class.java)
            intent.putExtra("solicitud", solicitud)
            startActivity(intent)
        }
        else{
            Toast.makeText(this, "Por favor seleccione una dirección", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSolicitud(): Solicitud{
        val ordenes = MainActivity.getCarrito()
        val total = getProductosSubtotal() + (getProductosSubtotal() * 0.1)
        val estado = "No entregado"
        val direccion = direccionSeleccionada
        val fecha = java.time.LocalDateTime.now()
        return Solicitud(0, ordenes, total, fecha, estado, direccion)
    }

    private fun initDireccion() {
        val btnSeleccionarDireccion = findViewById<Button>(R.id.btnSeleccionarDireccion)
        btnSeleccionarDireccion.setOnClickListener {
            val dialog = SeleccionarDireccion()
            dialog.show(supportFragmentManager, "SeleccionarDireccionDialog")
        }
    }

    override fun onDireccionSeleccionada(direccion: String) {
        direccionSeleccionada = direccion
        tvDireccion.text = direccion
    }

    private fun requestPermissions(context: Activity, permiso: String, justificacion:String, idCode:Int){
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            procederPago()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Data.MY_PERMISSIONS_REQUEST_LOCATION -> {
                //val textView = findViewById<TextView>(R.id.resultadosContactosTextView)
                var mensaje = ""
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    procederPago()
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
        btnPagar.isEnabled = false
        btnPagar.isClickable = false
        btnPagar.background = ContextCompat.getDrawable(this, R.drawable.button_disabled)
        Toast.makeText(this, "No puede seguir sin dar permiso de ubicación", Toast.LENGTH_SHORT).show()
    }
}