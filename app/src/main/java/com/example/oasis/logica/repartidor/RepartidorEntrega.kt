package com.example.oasis.logica.repartidor

import android.Manifest
import android.app.Activity
import android.app.UiModeManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.oasis.BuildConfig
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.databinding.ActivityRepartidorEntregaBinding
import com.example.oasis.datos.Data
import com.example.oasis.logica.ListaEstadoSolicitud
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.UIHelper
import com.example.oasis.model.Order
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.TilesOverlay

class RepartidorEntrega : AppCompatActivity() {
    private var fotoTomada = false
    private lateinit var btnFoto: ImageButton
    private lateinit var btnFinalizar: Button
    lateinit var photoUri: Uri

    private val reportedMeters: Int = 20
    private val normalZoom = 18.0
    private val extraZoom = 20.0
    private val locationUpdateInterval:Long = 10000
    private val minLocationUpdateInterval:Long = 5000
    private val minLight = 1500
    private val noAvaliableMarkerName = "Cargando dirección..."
    private val ordenesRecogidas = 0

    private lateinit var solicitud: Solicitud
    private lateinit var dataBaseSimulator: DataBaseSimulator
    private lateinit var binding: ActivityRepartidorEntregaBinding
    private lateinit var map: MapView
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var roadManager: RoadManager
    private var roadOverlay: Polyline? = null
    private lateinit var currentLocationMarker: Marker
    private lateinit var deliveryMarker : Marker
    private lateinit var ordenes : List<Order>
    private lateinit var ordenesMarkers: MutableList<Marker>
    private lateinit var mapController: IMapController
    private lateinit var currentLocation: Location


    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var lightSensorListener: SensorEventListener
    private var lightSensorInitialized = false
    private lateinit var barometerSensor: Sensor
    private lateinit var barometerSensorListener: SensorEventListener
    private var lastAltitude: Float = 0.0f

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            binding.ibSolicitudNoEntregada.setImageURI(photoUri)
            // Guardar en galeria
            saveImageToGallery(photoUri)
            fotoTomada = true
            AppUtilityHelper.deleteTempFiles(this)
        } else {
            Toast.makeText(this, "Captura de imagen cancelada", Toast.LENGTH_SHORT).show()
        }
    }

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
        setContentView(R.layout.activity_repartidor_entrega)

        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Ruta de entrega")

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID)
        binding = ActivityRepartidorEntregaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        map = binding.osmMap
        mapController = map.controller
        roadManager = OSRMRoadManager(this, "ANDROID")

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
                    currentLocationMarker.icon = ContextCompat.getDrawable(this, R.drawable.marker_user_icon)
                    updateMarker(it)
                    adjustMarkerSize(currentLocationMarker)

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
    }

    private fun initLocationCallBack(){
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null &&
                    currentLocation.distanceTo(location) > reportedMeters) {

                    updateMarker(location)
                    currentLocation = location

                    actualizarSolicitudRepartidorUbicacion()
                    checkProximityAndPickupOrders()
                }
            }
        }
    }

    private fun updateMarker(location: Location){
        GeoPoint(location.latitude, location.longitude).let {
            mapController.setZoom(normalZoom)
            mapController.setCenter(it)
            currentLocationMarker.position = it
            currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            currentLocationMarker.title = "Ubicación actual"
            map.invalidate() // Refresh the map
            Log.d("Location", "Location updated: ${location.latitude}, ${location.longitude}")
        }
    }

    private fun actualizarSolicitudRepartidorUbicacion(){
        solicitud.setRepartidorLatitud(currentLocation.latitude)
        solicitud.setRepartidorLongitud(currentLocation.longitude)
        dataBaseSimulator.actualizarSolicitud(solicitud)
    }

    private fun checkProximityAndPickupOrders() {
        val iterator = ordenesMarkers.iterator()
        while (iterator.hasNext()) {
            val marker = iterator.next()
            val order = ordenes.firstOrNull {
                it.getProducto().getProductoLatitud() == marker.position.latitude &&
                        it.getProducto().getProductoLongitud() == marker.position.longitude
            }
            if (order != null) {
                val orderLocation = Location("").apply {
                    latitude = order.getProducto().getProductoLatitud()
                    longitude = order.getProducto().getProductoLongitud()
                }
                if (currentLocation.distanceTo(orderLocation) <= 30) {
                    order.setEstadoOrden("Recogido")
                    map.overlays.remove(marker)
                    actualizarOrdenDeSolicitud(order)
                    iterator.remove()
                    Toast.makeText(this, "Orden ${order.getProducto().getNombre()} recogida", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun actualizarOrdenDeSolicitud(order: Order){
        solicitud.getOrdenes().forEach {
            if (it.getProducto().getId() == order.getProducto().getId()){
                it.setEstadoOrden(order.getEstadoOrden())
            }
        }
        dataBaseSimulator.actualizarSolicitud(solicitud)
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
        initFinalizar(solicitud)
        initMap()
        initRoute(solicitud)
        initLuminositySensor()
        initBarometerSensor()
        initButtons()
    }

    private fun initMap(){
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(false)
        map.overlays.add(currentLocationMarker)
        val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        if(uiManager.nightMode == UiModeManager.MODE_NIGHT_YES)
            binding.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)


        initMapListeners()
        initMapHelpers()
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
        for (marker in ordenesMarkers){
            if (marker != null) adjustMarkerSize(marker)
        }
    }

    private fun initMapHelpers(){
        map.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }))
    }

    private fun initRoute(solicitud: Solicitud){
        deliveryMarker = Marker(map)
        deliveryMarker.icon = ContextCompat.getDrawable(this, R.drawable.marker_delivery_icon)
        val deliveryLocation = GeoPoint(solicitud.getUbicacion().getLatitud(), solicitud.getUbicacion().getLongitud())
        deliveryMarker.position = deliveryLocation
        deliveryMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        deliveryMarker.title = "Entrega"
        map.overlays.add(deliveryMarker)

        val start = GeoPoint(currentLocation.latitude, currentLocation.longitude)
        val finish = GeoPoint(deliveryLocation)
        ordenes = solicitud.getOrdenes()

        val ordenesCoordenadas = initOrders()
        createRoute(start, finish, ordenesCoordenadas)

    }

    private fun initOrders() : MutableList<GeoPoint>{
        val ordenesPorCercania = ordenes.sortedBy { AppUtilityHelper.distanceBetweenTwoPoints(currentLocation.latitude, currentLocation.longitude, it.getProducto().getProductoLatitud(), it.getProducto().getProductoLongitud()) }
        ordenesMarkers = mutableListOf()
        val ordenesCoordenadas : MutableList<GeoPoint> = mutableListOf()

        for (orden in ordenesPorCercania){
            val marker = Marker(map)
            loadProductImageToIcon(this, orden.getProducto().getImagen(), marker)
            //marker.icon = ContextCompat.getDrawable(this, R.drawable.marker_normal_icon)
            val geoPoint = GeoPoint(orden.getProducto().getProductoLatitud(), orden.getProducto().getProductoLongitud())
            marker.position = geoPoint
            ordenesCoordenadas.add(geoPoint)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = orden.getProducto().getNombre()
            map.overlays.add(marker)
            ordenesMarkers.add(marker)
        }
        return ordenesCoordenadas
    }

    private fun loadProductImageToIcon(context: Activity, imageUrl : String, marker: Marker){
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .override(100, 100)
            .placeholder(R.drawable.marker_normal_icon)
            .error(R.drawable.marker_normal_icon)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    // Cuando la imagen esté lista, creamos un Drawable y lo asignamos al icono del marker
                    val drawableIcon = BitmapDrawable(context.resources, resource)
                    marker.icon = drawableIcon
                    adjustMarkerSize(marker)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Opción por si quieres mostrar algo cuando la imagen no está disponible
                    marker.icon = ContextCompat.getDrawable(context, R.drawable.marker_normal_icon)
                    adjustMarkerSize(marker)
                }
            })
    }

    private fun initLuminositySensor(){
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!
            lightSensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val light = event.values[0]
                    if(light < minLight){
                        binding.osmMap.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
                    }else{
                        binding.osmMap.overlayManager.tilesOverlay.setColorFilter(null)
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
            }
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
            lightSensorInitialized = true
        }
        else {
            Log.d("Luminosity", "No light sensor available")
        }
    }

    private fun initBarometerSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)!!
        barometerSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val pressure = event.values[0]
                val altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure)
                if (altitude != lastAltitude) {
                    lastAltitude = altitude
                    val altitudeStr = String.format("%.2f", altitude)
                    Toast.makeText(this@RepartidorEntrega, "Altura registrada: $altitudeStr m.s.n.m", Toast.LENGTH_LONG).show()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
        sensorManager.registerListener(barometerSensorListener, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun initResumen(solicitud: Solicitud){
        val distanciaTotal = findViewById<TextView>(R.id.tvSolicitudProductosRecogidos)
        val comision = findViewById<TextView>(R.id.tvSolicitudComision)
        val solicitudDireccion = findViewById<TextView>(R.id.tvSolicitudDireccion)

        distanciaTotal.text = ordenesRecogidas.toString()
        comision.text = solicitud.getTotal().times(0.1).toString()
        solicitudDireccion.text = solicitud.getUbicacion().getDireccion()
    }

    private fun initButtons(){
        btnFoto = findViewById(R.id.ibSolicitudNoEntregada)

        btnFoto.setOnClickListener {
            requestCameraPermission(this)
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

        binding.btnEstadoSolicitud.setOnClickListener {
            val dialog = ListaEstadoSolicitud(solicitud)
            dialog.show(supportFragmentManager, "ListaEstadoSolicitudDialog")
        }

    }

    private fun initFinalizar(solicitud: Solicitud){
        btnFinalizar = findViewById(R.id.btnRepartidorSolicitudFinalizar)
        val rbProductosRecogidos = true
        btnFinalizar.setOnClickListener {
            if (fotoTomada && rbProductosRecogidos){
                solicitud.setEstado("Entregado")
                dataBaseSimulator.actualizarSolicitud(solicitud)
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
        val tmpPictureFile = AppUtilityHelper.createTempPictureFile(this)
        photoUri = FileProvider.getUriForFile(this, "com.example.oasis.fileprovider", tmpPictureFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun createRoute(start: GeoPoint, finish: GeoPoint, ordenesCoordenadas: MutableList<GeoPoint>){
        CoroutineScope(Dispatchers.IO).launch {
            val routePoints = ArrayList<GeoPoint>()
            routePoints.add(start)
            routePoints.addAll(ordenesCoordenadas)
            routePoints.add(finish)
            val road = roadManager.getRoad(routePoints)

            withContext(Dispatchers.Main) {
                if (binding.osmMap != null) {
                    roadOverlay?.let { binding.osmMap.overlays.remove(it) }
                    roadOverlay = RoadManager.buildRoadOverlay(road)
                    roadOverlay?.outlinePaint?.color = Color.RED
                    roadOverlay?.outlinePaint?.strokeWidth = 10f
                    binding.osmMap.overlays.add(roadOverlay)
                    val distanciaStr = String.format("%.2f", road.mLength)
                    //Toast.makeText(this@MapActivity, "Distancia ruta: $distanciaStr km", Toast.LENGTH_LONG).show()
                }
            }
        }
        Toast.makeText(this, "Creando ruta...", Toast.LENGTH_LONG).show()
    }

    private fun saveImageToGallery(imageUri: Uri?) {
        if (imageUri != null) {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // Para Android 10 y versiones posteriores
                }

                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    contentResolver.openOutputStream(uri).use { outputStream ->
                        outputStream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al guardar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        mapController = map.controller
        mapController.setZoom(normalZoom)

        if (lightSensorInitialized) sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
        stopLocationUpdates()

        if (lightSensorInitialized) sensorManager.unregisterListener(lightSensorListener)
        sensorManager.unregisterListener(barometerSensorListener)
    }

    private fun stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback)
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

    private fun requestCameraPermission(context: Activity){
        val permiso = android.Manifest.permission.CAMERA
        val idCode = Data.MY_PERMISSIONS_REQUEST_CAMERA
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            requestStoragePermission(context)
        }
    }

    private fun requestStoragePermission(context: Activity){
        val permiso = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        val idCode = Data.MY_PERMISSIONS_REQUEST_STORAGE
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            // Si el permiso no ha sido concedido, lo solicitamos
            ActivityCompat.requestPermissions(context, arrayOf(permiso), idCode)
        } else {
            // Permiso de almacenamiento concedido
            tomarFoto()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Data.MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initUserLocation()
                } else {
                    Toast.makeText(this, "Necesita la localización para terminar", Toast.LENGTH_SHORT).show()
                    denegarFuncionalidad()
                }
                return
            }
            Data.MY_PERMISSIONS_REQUEST_CAMERA ->{
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestStoragePermission(this)
                } else {
                    Toast.makeText(this, "Necesita la cámara para finalizar", Toast.LENGTH_SHORT).show()
                    denegarFuncionalidad()
                }
            }
            Data.MY_PERMISSIONS_REQUEST_STORAGE ->{
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    tomarFoto()
                } else {
                    Toast.makeText(this, "Necesita el almacenamiento para finalizar", Toast.LENGTH_SHORT).show()
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