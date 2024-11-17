package com.example.oasis.logica.comprador

import DireccionesAdapter
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.datos.Data
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.db.FireBaseDataBase
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.FieldValidatorHelper
import com.example.oasis.logica.utility.UIHelper
import java.io.File
import java.util.Date
import java.util.Locale

class CompradorPerfil : AppCompatActivity() {
    private lateinit var btnFotoPerfil: ImageButton

    lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_perfil)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Perfil")
        initUI()
        initSalir()
    }

    private fun initUI(){
        val tvNombre = findViewById<EditText>(R.id.etNombrePerfil)
        val tvCorreo = findViewById<EditText>(R.id.etCorreoPerfil)
        val lvDirecciones = findViewById<ListView>(R.id.lvDireccionesPerfil)

        tvNombre.setText(CompradorInicio.comprador.getNombre())
        tvCorreo.setText(CompradorInicio.comprador.getEmail())
        lvDirecciones.adapter = DireccionesAdapter(this, CompradorInicio.comprador.getDirecciones())


        initFotoPerfilButton()
        initEdicionPerfil()
    }

    private fun initSalir(){
        val btnSalir = findViewById<TextView>(R.id.btnSalir)

        btnSalir.setOnClickListener {
            FireBaseDataBase().logout()
            MainActivity.clearCarrito()
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun initEdicionPerfil(){
        val btnEditarPerfil = findViewById<Button>(R.id.btnEditar)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        val tvNombre = findViewById<EditText>(R.id.etNombrePerfil)
        val tvCorreo = findViewById<EditText>(R.id.etCorreoPerfil)
        val tvDescripcionCambiarFoto = findViewById<TextView>(R.id.tvDescripcionCambiarFoto)

        btnEditarPerfil.setOnClickListener {
            btnEditarPerfil.isEnabled = false
            btnEditarPerfil.isClickable = false
            habilitarEdicionPerfil(tvNombre, tvCorreo, btnGuardar)
            tvDescripcionCambiarFoto.visibility = TextView.VISIBLE
        }

        btnGuardar.setOnClickListener {
            if (guardarCambios(tvNombre, tvCorreo)){
                btnEditarPerfil.isEnabled = true
                btnEditarPerfil.isClickable = true
                deshabilitarEdicionPerfil(tvNombre, tvCorreo, btnGuardar)
                tvDescripcionCambiarFoto.visibility = TextView.INVISIBLE
            }
        }
    }

    private fun guardarCambios(tvNombre: TextView, tvCorreo: TextView): Boolean{
        val nombre = tvNombre.text.toString()
        val correo = tvCorreo.text.toString()
        var cambiosCorrectos = false

        if(nombre.isEmpty() || correo.isEmpty()){
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
        }
        if (!FieldValidatorHelper().validateEmail(correo)){
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
        }
        else{
            val compradorTmp = CompradorInicio.comprador
            compradorTmp.setNombre(nombre)
            val dataBaseSimulator = DataBaseSimulator(this)
            if (dataBaseSimulator.actualizarComprador(compradorTmp)) {
                CompradorInicio.comprador.setNombre(nombre)
            }
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
            cambiosCorrectos = true
        }
        return cambiosCorrectos
    }

    private fun habilitarEdicionPerfil(tvNombre: TextView, tvCorreo: TextView, btnGuardar: Button){
        tvNombre.isEnabled = true
        tvNombre.setTextColor(ContextCompat.getColor(this, R.color.black))
        /*tvCorreo.isEnabled = true
        tvCorreo.setTextColor(ContextCompat.getColor(this, R.color.black))*/
        btnFotoPerfil.isEnabled = true
        btnFotoPerfil.isClickable = true
        btnGuardar.isEnabled = true
        btnGuardar.isClickable = true
    }

    private fun deshabilitarEdicionPerfil(tvNombre: TextView, tvCorreo: TextView, btnGuardar: Button){
        tvNombre.isEnabled = false
        tvNombre.setTextColor(ContextCompat.getColor(this, R.color.white))
        /*tvCorreo.isEnabled = false
        tvCorreo.setTextColor(ContextCompat.getColor(this, R.color.white))*/
        btnFotoPerfil.isEnabled = false
        btnFotoPerfil.isClickable = false
        btnGuardar.isEnabled = false
        btnGuardar.isClickable = false
    }


    private fun initFotoPerfilButton(){
        btnFotoPerfil = findViewById(R.id.FotoPerfilbtn)
        btnFotoPerfil.isEnabled = false
        btnFotoPerfil.isClickable = false
        btnFotoPerfil.setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Data.MY_PERMISSIONS_REQUEST_CAMERA)
        }
        else{
            // Permiso de camara concedido, pedir el de galeria
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Data.MY_PERMISSIONS_REQUEST_STORAGE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Data.MY_PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                } else {
                    denegarFuncionalidad()
                }
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Data.MY_PERMISSIONS_REQUEST_STORAGE)
            }
            Data.MY_PERMISSIONS_REQUEST_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                    showImageChooser()
                } else {
                    denegarFuncionalidad()
                }
            }
        }
    }

    private fun denegarFuncionalidad() {
        Toast.makeText(this, "Por favor acepte los permisos para cambiar la foto", Toast.LENGTH_SHORT).show()
    }

    private fun showImageChooser() {
        val chooserIntent = Intent.createChooser(Intent(), "Selecciona una opción")
        val intentArray = mutableListOf<Intent>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val tmpPictureFile = AppUtilityHelper.createTempPictureFile(this)
            photoUri = FileProvider.getUriForFile(this, "com.example.oasis.fileprovider", tmpPictureFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intentArray.add(takePictureIntent)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intentArray.add(pickPhotoIntent)
        }

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray.toTypedArray())

        // Lanzar el chooser
        imageChooserLauncher.launch(chooserIntent)
    }

    // Registrar el ActivityResultLauncher para manejar el resultado
    private val imageChooserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            // Verificar si viene de la cámara o la galería
            if (data != null && data.data != null) {
                // Imagen desde galería
                val selectedImageUri = data.data
                btnFotoPerfil.setImageURI(selectedImageUri)
            } else {
                // Imagen desde la cámara
                btnFotoPerfil.setImageURI(photoUri)
            }
        }
        AppUtilityHelper.deleteTempFiles(this)
    }
}