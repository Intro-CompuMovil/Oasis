package com.example.oasis.logica.comprador

import android.Manifest
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.oasis.BuildConfig
import com.example.oasis.R
import com.example.oasis.databinding.ActivityCompradorSolicitudNoEntregadaBinding
import com.example.oasis.datos.Data
import com.example.oasis.logica.ListaEstadoSolicitud
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Repartidor
import com.example.oasis.model.Solicitud
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay

class CompradorSolicitudNoEntregada : AppCompatActivity() {

    private val reportedMeters: Int = 20
    private val normalZoom = 18.0
    private val extraZoom = 20.0
    private val locationUpdateInterval:Long = 10000
    private val minLocationUpdateInterval:Long = 5000
    private val minLight = 1500

    private lateinit var binding: ActivityCompradorSolicitudNoEntregadaBinding
    private lateinit var solicitud: Solicitud
    private lateinit var dataBaseSimulator: DataBaseSimulator
    private lateinit var solicitudRef: DatabaseReference
    private lateinit var solicitudEventListener: ValueEventListener
    private lateinit var map: MapView
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var currentLocationMarker: Marker
    private lateinit var deliveryMarker : Marker
    private lateinit var mapController: IMapController
    private lateinit var currentLocation: Location
    private lateinit var repartidorLocation: Location
    private lateinit var repartidorMarker: Marker

    private val getLocationSettings = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if(result.resultCode == RESULT_OK){
            startLocationUpdates()
        }else{
            Toast.makeText(this, "No se puede obtener la ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_solicitud_no_entregada)

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID)
        binding = ActivityCompradorSolicitudNoEntregadaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        map = binding.osmMap
        mapController = map.controller

        UIHelper().setupFooter(this)
        dataBaseSimulator = DataBaseSimulator(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationRequest = createLocationRequest()
        requestLocationPermission(this)
    }

    private fun initUserLocation(){
        try {
            mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = it
                    currentLocationMarker = Marker(map)
                    currentLocationMarker.icon = ContextCompat.getDrawable(this, R.drawable.marker_normal_icon)
                    updateMarker(it)
                    adjustMarkerSize(currentLocationMarker)
                    mapController.setZoom(normalZoom)
                    mapController.setCenter(currentLocationMarker.position)

                    initActivity()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "No se puede obtener la ubicación", Toast.LENGTH_SHORT).show()
            Log.e("Location", "Error: ${e.message}")
        }
    }

    private fun initActivity(){
        initUI()
        initLocationCallBack()
        checkLocationSettings()

        initActualicacionesSolicitud()
    }

    private fun initLocationCallBack(){
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null &&
                    currentLocation.distanceTo(location) > reportedMeters) {

                    updateMarker(location)
                    currentLocation = location
                }
            }
        }
    }

    private fun updateMarker(location: Location){
        GeoPoint(location.latitude, location.longitude).let {
            currentLocationMarker.position = it
            currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            currentLocationMarker.title = "Ubicación actual"
            map.invalidate() // Refresh the map
            Log.d("Location", "Location updated: ${location.latitude}, ${location.longitude}")
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
    }

    private fun checkLocationSettings(){
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            startLocationUpdates()
        }

        task.addOnFailureListener { e ->
            if ((e as ApiException).statusCode == CommonStatusCodes.RESOLUTION_REQUIRED){
                val resolvable = e as ResolvableApiException
                val isr = IntentSenderRequest.Builder(resolvable.resolution).build()
                getLocationSettings.launch(isr)
            }else{
                Toast.makeText(this, "No se puede obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createLocationRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,locationUpdateInterval).apply {
            setMinUpdateIntervalMillis(minLocationUpdateInterval)
        }.build()

    private fun adjustMarkerSize(marker: Marker) {
        val zoomLevel = map.zoomLevelDouble
        val scaleFactor = zoomLevel / 20.0 // Adjust the divisor to control scaling
        val icon = marker.icon
        icon?.setBounds(0, 0, (icon.intrinsicWidth * scaleFactor).toInt(), (icon.intrinsicHeight * scaleFactor).toInt())
        marker.icon = icon
    }

    private fun initUI(){
        solicitud = intent.getSerializableExtra("solicitud") as Solicitud
        initResumen(solicitud)
        if (solicitud.getRepartidor() != null){
            cargarRepartidorEnResumen(solicitud.getRepartidor()!!)
        }

        initDeliveryMarker()

        initButtons()
        initMap()
    }

    private fun initResumen(solicitud: Solicitud){
        val solicitudTotal = findViewById<TextView>(R.id.tvSolicitudCostoTotal)
        val estado = findViewById<TextView>(R.id.tvSolicitudEstado)

        solicitudTotal.text = solicitud.getTotal().toString()
        estado.text = solicitud.getEstado()
    }

    private fun initDeliveryMarker(){
        deliveryMarker = Marker(map)
        deliveryMarker.icon = ContextCompat.getDrawable(this, R.drawable.marker_delivery_icon)
        deliveryMarker.position = GeoPoint(solicitud.getUbicacion().getLatitud(), solicitud.getUbicacion().getLongitud())
        deliveryMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        deliveryMarker.title = "Ubicación de entrega"
        map.overlays.add(deliveryMarker)
    }

    private fun initButtons(){
        binding.btnEstadoSolicitud.setOnClickListener {
            val dialog = ListaEstadoSolicitud(solicitud)
            dialog.show(supportFragmentManager, "ListaEstadoSolicitudDialog")
        }

        binding.locationButton.setOnClickListener {
            mapController.animateTo(currentLocationMarker.position)
        }

        binding.zoomInButton.setOnClickListener {
            mapController.zoomIn()
        }

        binding.zoomOutButton.setOnClickListener {
            mapController.zoomOut()
        }
    }

    private fun initMap(){
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(false)
        map.overlays.add(currentLocationMarker)
        val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if(uiManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            binding.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)


        initMapListeners()
    }

    private fun initActualicacionesSolicitud(){
        val ref = FirebaseDatabase.getInstance().getReference(Data.PATH_DATABASE_SOLICITUDES)
        solicitudRef = ref.child(solicitud.getId())

        solicitudEventListener = (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedSolicitud = snapshot.getValue(Solicitud::class.java)
                if (updatedSolicitud != null) {
                    checkActualizaciones(updatedSolicitud)
                    solicitud = updatedSolicitud
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FireBaseDataBase", "Error al obtener las solicitudes: $error")
            }
        })

        solicitudRef.addValueEventListener(solicitudEventListener)
    }

    private fun checkActualizaciones(updatedSolicitud: Solicitud){
        if (solicitud.getRepartidor() == null && updatedSolicitud.getRepartidor() != null){
            cargarRepartidorEnResumen(updatedSolicitud.getRepartidor()!!)
        }
        if (::repartidorLocation.isInitialized){
            actualizarPosicionRepartidor(updatedSolicitud)
        }else{
            if (updatedSolicitud.getRepartidorLatitud() != null && updatedSolicitud.getRepartidorLongitud() != null){
                repartidorLocation = Location("")
                repartidorLocation.latitude = updatedSolicitud.getRepartidorLatitud()!!
                repartidorLocation.longitude = updatedSolicitud.getRepartidorLongitud()!!

                actualizarPosicionSolicitud()
            }
        }
    }

    private fun cargarRepartidorEnResumen(repartidor: Repartidor){
        binding.tvSolicitudEstadoRepartidor.text = "Repartidor asignado: " + repartidor.getNombre()
        binding.ivSolicitudNoEntregada.setImageResource(R.drawable.user)
        val repartidorPhotoURL = repartidor.photoURL()
        if(repartidorPhotoURL.isNotEmpty()){
            Glide.with(this).load(repartidorPhotoURL).into(binding.ivSolicitudNoEntregada)
        }
    }

    private fun actualizarPosicionSolicitud(){
        repartidorMarker = Marker(map)
        repartidorMarker.icon = ContextCompat.getDrawable(this, R.drawable.marker_user_icon)
        repartidorMarker.position = GeoPoint(repartidorLocation.latitude, repartidorLocation.longitude)
        repartidorMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        repartidorMarker.title = "Ubicación del repartidor"
        map.overlays.add(repartidorMarker)
    }

    private fun actualizarPosicionRepartidor(updatedSolicitud: Solicitud){
        repartidorLocation.latitude = updatedSolicitud.getRepartidorLatitud()!!
        repartidorLocation.longitude = updatedSolicitud.getRepartidorLongitud()!!
        repartidorMarker.position = GeoPoint(repartidorLocation.latitude, repartidorLocation.longitude)
        repartidorMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.invalidate()
    }

    private fun initMapListeners(){
        map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                adjustMarkers()
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                adjustMarkers()
                return true
            }
        })
    }

    private fun adjustMarkers(){
        adjustMarkerSize(currentLocationMarker)
        adjustMarkerSize(deliveryMarker)
        if (::repartidorMarker.isInitialized){
            adjustMarkerSize(repartidorMarker)
        }
    }

    private fun requestPermissions(context: Activity, permiso: String, justificacion:String, idCode:Int){
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            initUI()
        }
    }

    private fun requestLocationPermission(context: Activity){
        val permiso = android.Manifest.permission.ACCESS_FINE_LOCATION
        val idCode = Data.MY_PERMISSIONS_REQUEST_LOCATION
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            initUserLocation()
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

    override fun onPause(){
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if(::solicitudRef.isInitialized){
            solicitudRef.removeEventListener(solicitudEventListener)
        }
    }
}