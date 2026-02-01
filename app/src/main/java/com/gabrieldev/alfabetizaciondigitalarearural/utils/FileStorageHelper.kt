package com.gabrieldev.alfabetizaciondigitalarearural.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileStorageHelper {
    fun guardarImagenDesdeUri(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            val directorioImagenes = File(context.filesDir, "imagenes_lecciones")
            if (!directorioImagenes.exists()) {
                directorioImagenes.mkdirs()
            }

            val nombreArchivo = "img_${UUID.randomUUID()}.jpg"
            val archivoDestino = File(directorioImagenes, nombreArchivo)
            
            FileOutputStream(archivoDestino).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            archivoDestino.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
