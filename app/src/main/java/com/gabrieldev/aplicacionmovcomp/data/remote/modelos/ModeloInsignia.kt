package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Colección Firestore: insignias/{idInsignia}
 * Catálogo global — administrado desde la consola Firebase.
 */
data class ModeloInsignia(
    val tipoLogro: String = "",
    val nombreVisible: String = "",
    val descripcion: String = "",
    val iconoRef: String = "",
    val condicionDesbloqueo: String = ""
)
