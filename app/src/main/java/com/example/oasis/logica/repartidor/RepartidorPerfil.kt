package com.example.oasis.logica.repartidor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.oasis.logica.utility.FieldValidatorHelper
import com.example.oasis.logica.utility.UIHelper
import java.io.File
import java.util.Date
import java.util.Locale

class RepartidorPerfil : AppCompatActivity() {

    private lateinit var btnFotoPerfil: ImageButton

    lateinit var photoUri: Uri

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            btnFotoPerfil.setImageURI(data?.data)
        }
    }

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            btnFotoPerfil.setImageURI(photoUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repartidor_perfil)
        UIHelper().setupRepartidorFooter(this)
        UIHelper().setupHeader(this, "Perfil")
        initUI()
        initSalir()
    }

    private fun initUI(){
        val tvNombre = findViewById<EditText>(R.id.etNombrePerfil)
        val tvCorreo = findViewById<EditText>(R.id.etCorreoPerfil)

        tvNombre.setText(MainActivity.getUsuarioNombre())
        tvCorreo.setText("user1@example.com")

        initFotoPerfilButton()
        initEdicionPerfil()
    }

    private fun initSalir(){
        val btnSalir = findViewById<TextView>(R.id.btnSalir)

        btnSalir.setOnClickListener {
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

        btnEditarPerfil.setOnClickListener {
            btnEditarPerfil.isEnabled = false
            btnEditarPerfil.isClickable = false
            habilitarEdicionPerfil(tvNombre, tvCorreo, btnGuardar)
        }

        btnGuardar.setOnClickListener {
            btnEditarPerfil.isEnabled = true
            btnEditarPerfil.isClickable = true
            guardarCambios(tvNombre, tvCorreo)
            deshabilitarEdicionPerfil(tvNombre, tvCorreo, btnGuardar)
        }
    }

    private fun guardarCambios(tvNombre: TextView, tvCorreo: TextView){
        val nombre = tvNombre.text.toString()
        val correo = tvCorreo.text.toString()

        if(nombre.isEmpty() || correo.isEmpty()){
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
        }
        if (FieldValidatorHelper().validateEmail(correo)){
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
        }
        else{
            MainActivity.setUsuarioNombre(nombre)
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun habilitarEdicionPerfil(tvNombre: TextView, tvCorreo: TextView, btnGuardar: Button){
        tvNombre.isEnabled = true
        tvCorreo.isEnabled = true
        btnFotoPerfil.isEnabled = true
        btnFotoPerfil.isClickable = true
        btnGuardar.isEnabled = true
        btnGuardar.isClickable = true
    }

    private fun deshabilitarEdicionPerfil(tvNombre: TextView, tvCorreo: TextView, btnGuardar: Button){
        tvNombre.isEnabled = false
        tvCorreo.isEnabled = false
        btnFotoPerfil.isEnabled = false
        btnFotoPerfil.isClickable = false
        btnGuardar.isEnabled = false
        btnGuardar.isClickable = false
    }


    private fun initFotoPerfilButton(){
        btnFotoPerfil = findViewById(R.id.FotoPerfilbtn)

        btnFotoPerfil.setOnClickListener {
            showImagePickerOptions()
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Tomar foto", "Elegir de la galería")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Selecciona una opción")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> requestCamera()
                1 -> requestStorage()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        createImageFile()
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun openGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickPhotoIntent ->
            pickImageLauncher.launch(pickPhotoIntent)
        }
    }

    private fun requestCamera(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), Data.MY_PERMISSIONS_REQUEST_CAMERA)
        }
        else{
            openCamera()
        }
    }

    private fun requestStorage(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Data.MY_PERMISSIONS_REQUEST_STORAGE)
        }
        else{
            openGallery()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).also {
            // Guardamos el archivo URI
            photoUri = FileProvider.getUriForFile(this, "com.example.oasis.fileprovider", it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Data.MY_PERMISSIONS_REQUEST_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                    openGallery()
                } else {
                    denegarFuncionalidad()
                }
            }
            Data.MY_PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido
                    openCamera()
                } else {
                    denegarFuncionalidad()
                }
            }
        }
    }

    private fun denegarFuncionalidad() {
        Toast.makeText(this, "Por favor acepta los permisos para cambiar la foto", Toast.LENGTH_SHORT).show()
    }
}