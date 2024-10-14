package com.example.oasis.logica.repartidor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oasis.R
import com.example.oasis.datos.Data
import com.example.oasis.logica.adapters.RepartidorInicioAdapter
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Repartidor
import com.example.oasis.model.Solicitud
import com.example.oasis.model.Ubicacion
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class RepartidorInicio : AppCompatActivity() {

    companion object {
        var repartidor: Repartidor = Repartidor(-1, "", "", "")

        fun actualizarRepartidor(repartidor: Repartidor, dataBaseSimulator: DataBaseSimulator) {
            this.repartidor = repartidor
            dataBaseSimulator.actualizarRepartidor(repartidor)
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var rvSolicitudes: RecyclerView
    private lateinit var solicitudes: List<Solicitud>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor_inicio)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Hola " + repartidor.getNombre())
        initUI()
    }

    private fun initUI() {
        initButtons()
        initSolicitudes()
    }

    private fun initButtons() {
        val btnOrganizarSolicitudes = findViewById<Button>(R.id.btnOrganizarSolicitudes)
        btnOrganizarSolicitudes.setOnClickListener {
            requestLocationPermission()
        }
    }

    private fun initSolicitudes() {
        rvSolicitudes = findViewById<RecyclerView>(R.id.rvRepartidorSolicitudesCercanas)
        rvSolicitudes.layoutManager = LinearLayoutManager(this)
        val dataBaseSimulator = DataBaseSimulator(this)
        solicitudes = dataBaseSimulator.getSolicitudesActivas()
        val haySolicitudActiva = solicitudes.find { it.getRepartidor()?.getEmail() == repartidor.getEmail() }
        if (haySolicitudActiva != null) {
            Intent(this, RepartidorEntrega::class.java).also {
                it.putExtra("solicitud", haySolicitudActiva)
                startActivity(it)
            }
        }else{
            loadSolicitudes()
        }
    }

    private fun loadSolicitudes() {
        if (hasLocationPermission()) {
            getLastLocation { location ->
                val ubicacion = Ubicacion(location?.latitude ?: 0.0, location?.longitude ?: 0.0, "")
                val solicitudesOrdenadas = orderSolicitudesByDistance(solicitudes, ubicacion)
                rvSolicitudes.adapter = RepartidorInicioAdapter(this, solicitudesOrdenadas, ubicacion)
            }
        } else {
            requestLocationPermission()
        }

    }

    private fun orderSolicitudesByDistance(solicitudes: List<Solicitud>, ubicacion: Ubicacion): List<Solicitud> {
        return solicitudes.sortedBy {
            AppUtilityHelper.distanceBetweenTwoPoints(
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                it.getUbicacion().getLatitud(),
                it.getUbicacion().getLongitud()
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Data.MY_PERMISSIONS_REQUEST_LOCATION)
    }

    private fun getLastLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                callback(location)
            }
        } else {
            callback(null)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Data.MY_PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initSolicitudes()
            }else{
                Toast.makeText(this, "Necesita dar permiso para ubicar solicitudes cercanas", Toast.LENGTH_LONG).show()
                if ( rvSolicitudes.adapter == null){
                    rvSolicitudes.adapter = RepartidorInicioAdapter(this, solicitudes, null)
                }
            }
        }
    }

}