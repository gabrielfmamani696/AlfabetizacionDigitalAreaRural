package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Colección Firestore: intentos_leccion/{idIntento}
 */
data class ModeloIntentoLeccion(
    val idUsuario: String = "",
    val idLeccion: String = "",
    val calificacionObtenida: Int = 0,
    val fechaIntento: Long = 0L,
    val completadoExitosamente: Boolean = false
)
