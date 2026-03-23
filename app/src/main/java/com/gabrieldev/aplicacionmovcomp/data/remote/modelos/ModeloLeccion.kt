package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Colección Firestore: lecciones/{uuidGlobal}
 */
data class ModeloLeccion(
    val titulo: String = "",
    val tema: String = "",
    val descripcion: String = "",
    val fechaCreacion: Long = 0L,
    val creadaPorUsuario: Boolean = false,
    val uuidAutorOriginal: String = "",
    val imagenUrl: String = "",
    val nivelRequerido: Int = 0,
    val idEspacio: String = "",
    val uuidCreador: String = ""
)
