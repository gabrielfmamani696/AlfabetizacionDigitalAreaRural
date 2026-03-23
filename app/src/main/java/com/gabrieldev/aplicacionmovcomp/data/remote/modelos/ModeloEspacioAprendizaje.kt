package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Colección Firestore: espacios_aprendizaje/{idEspacio}
 */
data class ModeloEspacioAprendizaje(
    val idDocenteCreador: String = "",
    val nombreEspacio: String = "",
    val tokenAcceso: String = ""
)
