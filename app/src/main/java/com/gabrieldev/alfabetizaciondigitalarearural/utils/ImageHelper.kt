package com.gabrieldev.alfabetizaciondigitalarearural.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File

object ImageHelper {

    fun imagenABase64(
        context: Context,
        rutaImagen: String?
    ): String? {
        if (rutaImagen.isNullOrEmpty()) return null

        try {
            val archivo = File(rutaImagen)
            if (!archivo.exists()) return null

            val bitmap = BitmapFactory.decodeFile(archivo.absolutePath)
            if (bitmap == null) return null

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun base64AImagen(
        context: Context,
        base64: String,
        nombreArchivo: String
    ): String? {
        try {
            val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bitmap == null) return null

            val directorioImagenes = File(context.filesDir, "imagenes")
            if (!directorioImagenes.exists()) {
                directorioImagenes.mkdirs()
            }

            val archivo = File(directorioImagenes, nombreArchivo)
            archivo.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            return archivo.absolutePath

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}