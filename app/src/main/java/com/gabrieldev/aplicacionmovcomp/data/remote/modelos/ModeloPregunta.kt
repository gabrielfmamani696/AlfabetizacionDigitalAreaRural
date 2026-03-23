package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Subcolección Firestore: cuestionarios/{idCuestionario}/preguntas/{idPregunta}
 */
data class ModeloPregunta(
    val idCuestionario: String = "",
    val enunciado: String = ""
)
