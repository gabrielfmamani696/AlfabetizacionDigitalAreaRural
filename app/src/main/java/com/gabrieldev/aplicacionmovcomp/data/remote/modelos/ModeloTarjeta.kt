package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Subcolección Firestore: lecciones/{idLeccion}/tarjetas/{idTarjeta}
 */
data class ModeloTarjeta(
    val idLeccion: String = "",
    val ordenSecuencia: Int = 0,
    val contenidoTexto: String = "",
    val tipoFondo: String = "",
    val dataFondo: String = ""
)
