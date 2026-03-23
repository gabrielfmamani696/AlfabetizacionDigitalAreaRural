package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Subcolección Firestore: usuarios/{uuidUsuario}/logros_notificados/{idLogro}
 */
data class ModeloLogroNotificado(
    val idUsuario: String = "",
    val idInsignia: String = "",
    val fechaNotificacion: Long = 0L
)
