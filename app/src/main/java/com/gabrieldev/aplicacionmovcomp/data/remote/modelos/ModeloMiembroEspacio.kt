package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Colección Firestore: miembros_espacio/{idDocumento}
 * PK compuesta: idEspacio + idEstudiante
 */
data class ModeloMiembroEspacio(
    val idEspacio: String = "",
    val idEstudiante: String = ""
)
