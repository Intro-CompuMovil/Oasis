package com.example.oasis.logica.utility

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.util.Date
import java.util.Locale

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
    }
}