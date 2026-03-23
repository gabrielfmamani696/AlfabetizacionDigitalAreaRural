package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Colección Firestore: usuarios/{uuidUsuario}
 * El ID del documento ES el uuidUsuario generado localmente.
 */
data class ModeloUsuario(
    val alias: String = "",
    val puntosTotales: Int = 0,
    val rachaActualDias: Int = 0,
    val ultimaActividad: Long = 0L,
    val rolDocente: Boolean = false,
    val idAvatar: String = "",
    val nombre: String = "",
    val apellido: String = ""
)
