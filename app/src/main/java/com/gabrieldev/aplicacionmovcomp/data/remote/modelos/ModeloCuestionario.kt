package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Subcolección Firestore: lecciones/{idLeccion}/cuestionarios/{idCuestionario}
 */
data class ModeloCuestionario(
    val idLeccion: String = "",
    val tituloQuiz: String = ""
)
