package com.example.oasis.logica.utility

import android.app.AlertDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.oasis.model.Product
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class AppUtilityHelper {
    companion object{
        fun deleteTempFiles(context: Context) {
            val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Temp")
            storageDir?.listFiles()?.forEach { file ->
                if (file.isFile && file.name.startsWith("JPEG_")) {
                    file.delete()
                }
            }
        }

        fun createTempPictureFile(context: Context): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Temp")
            if (!storageDir.exists()) {
                storageDir.mkdirs() // Crear el directorio si no existe
            }
            return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        }

        fun loadJSONFromAsset(context: Context, fileName: String): String? {
            return try {
                val inputStream = context.assets.open(fileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charsets.UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
        }

        fun loadJSONFromExternalStorage(context: Context, fileName: String): String? {
            return try {
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                if (file.exists()) {
                    file.readText(Charsets.UTF_8)
                } else {
                    file.writeText("[]")
                    "[]"
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
        }

        fun saveJSONToExternalStorage(context: Context, fileName: String, jsonString: String) {
            try {
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                file.writeText(jsonString)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }

        fun distanceBetweenTwoPoints(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val latDistance = Math.toRadians(lat2 - lat1)
            val lngDistance = Math.toRadians(lon2 - lon1)
            val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2))
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val result = 6371 * c
            return (result * 100.0).roundToInt() / 100.0
        }

        fun showErrorDialog(context: Context, errorMessage: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Error")
            builder.setMessage(errorMessage)
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        fun cargarProductoImagen(imageProduct: ImageView, product: Product, context: Context){
            if (!product.getImagen().isNullOrEmpty()) {
                Glide.with(context)
                    .load(product.getImagen())
                    .error(imageProduct.drawable)
                    .into(imageProduct)
            }
        }
    }
}