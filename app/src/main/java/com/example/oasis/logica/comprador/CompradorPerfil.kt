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
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.oasis.MainActivity
import com.example.oasis.R
import com.example.oasis.datos.Data
import com.example.oasis.logica.db.DataBaseSimulator
import com.example.oasis.logica.db.FireBaseDataBase
import com.example.oasis.logica.utility.AppUtilityHelper
import com.example.oasis.logica.utility.FieldValidatorHelper
import com.example.oasis.logica.utility.UIHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.util.Date
import java.util.Locale

class CompradorPerfil : AppCompatActivity() {
    private lateinit var btnFotoPerfil: ImageButton

    //FotoPerfilbtn
    lateinit var photoUri: Uri

    // declarar variables de firebase

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseDatabase

    // mirar si la imagen esta cargada
    private var imagenCargada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprador_perfil)

        UIHelper().setupFooter(this)
        UIHelper().setupHeader(this, "Perfil")

        // instanciar var de firebase
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        val currentUser = auth.currentUser

        initUI()
        initSalir()
    }

    private suspend fun uploadImageToFirebaseStorage(userId: String): String {
        var photoURL = ""
        if (photoUri != null) {
            val storageRef = storage.reference.child("${Data.MY_PERMISSIONS_REQUEST_STORAGE}$userId.jpg")
            try {
                storageRef.putFile(photoUri).await()
                Log.d("Registro", "Successfully uploaded image")
                photoURL = storageRef.downloadUrl.await().toString()
            } catch (e: Exception) {
                Log.e("Registro", "Failed to upload image: ${e.message}")
            }
            AppUtilityHelper.deleteTempFiles(this)
        }
        return photoURL
    }


    private fun initUI(){
        val tvNombre = findViewById<EditText>(R.id.etNombrePerfil)
        val tvCorreo = findViewById<EditText>(R.id.etCorreoPerfil)
        val lvDirecciones = findViewById<ListView>(R.id.lvDireccionesPerfil)

        tvNombre.setText(CompradorInicio.comprador.getNombre())
        tvCorreo.setText(CompradorInicio.comprador.getEmail())
        lvDirecciones.adapter = DireccionesAdapter(this, CompradorInicio.comprador.getDirecciones())
        // Verificar si el campo photoURL tiene un URL válido
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.reference.child("compradores").child(userId)
            userRef.child("photoURL").get().addOnSuccessListener { dataSnapshot ->
                val photoURL = dataSnapshot.getValue(String::class.java) // Obtener el valor del campo
                if (!photoURL.isNullOrEmpty()) {
                    // Usar Glide para cargar la imagen desde el URL (más eficiente)
                    Glide.with(this)
                        .load(photoURL)
                        .into(btnFotoPerfil)
                }
            }.addOnFailureListener {
                Log.e("compradoresPerfil", "Error al obtener el campo photoURL: ${it.message}")
            }
        }

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
            lifecycleScope.launch {
                val userId = auth.currentUser?.uid
                if (guardarCambios(tvNombre, tvCorreo)) {
                    if (userId != null && this@CompradorPerfil::photoUri.isInitialized) {
                        // Subir la imagen a Firebase Storage y guardar el URL
                        val photoURL = uploadImageToFirebaseStorage(userId)
                        if (photoURL.isNotEmpty()) {
                            val userRef = database.reference.child("compradores").child(userId)
                            userRef.child("photoURL").setValue(photoURL)
                            Toast.makeText(this@CompradorPerfil, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@CompradorPerfil, "Error al subir la imagen1", Toast.LENGTH_LONG).show()
                        }
                    }
                    btnEditarPerfil.isEnabled = true
                    btnEditarPerfil.isClickable = true
                    deshabilitarEdicionPerfil(tvNombre, tvCorreo, btnGuardar)
                    tvDescripcionCambiarFoto.visibility = TextView.INVISIBLE
                }
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
        else {
            val userId = auth.currentUser?.uid
            cambiosCorrectos = false
            if (userId != null) {

                val userRef = database.reference.child("compradores").child(userId)
                cambiosCorrectos = true

                // Actualizar el nombre
                userRef.child("nombre").setValue(nombre)
                /*.addOnSuccessListener {
                    Log.d("compradoresPerfil", "Nombre actualizado correctamente")


                }
                .addOnFailureListener {
                    Log.e("compradoresrPerfil", "Error al actualizar el nombre: ${it.message}")
                }
                */


                // Actualizar el correo
                userRef.child("email").setValue(correo)
                /*
                .addOnSuccessListener {
                    Log.d("compradoresPerfil", "Correo actualizado correctamente")
                    cambiosCorrectos = true
                    Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.e("RepartidorPerfil", "Error al actualizar el correo: ${it.message}")
                }

                 */
                //auth.currentUser?.updateEmail(correo)


            }
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

            lifecycleScope.launch {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    // Verificar si la imagen viene de la galería
                    if (data != null && data.data != null) {
                        val selectedImageUri = data.data
                        btnFotoPerfil.setImageURI(selectedImageUri)
                        photoUri = selectedImageUri!!
                    } else {
                        // Imagen capturada desde la cámara
                        btnFotoPerfil.setImageURI(photoUri)
                    }

                    // Subir la imagen a Firebase Storage y guardar el URL en Firebase Database
                    val photoURL = uploadImageToFirebaseStorage(userId)
                    if (photoURL.isNotEmpty()) {
                        val userRef = database.reference.child("compradores").child(userId)
                        userRef.child("photoURL").setValue(photoURL)
                        Toast.makeText(this@CompradorPerfil, "Foto actualizada correctamente", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@CompradorPerfil, "Error al subir la imagen2", Toast.LENGTH_LONG).show()
                    }
                }
            }
            AppUtilityHelper.deleteTempFiles(this)
        }
    }

}