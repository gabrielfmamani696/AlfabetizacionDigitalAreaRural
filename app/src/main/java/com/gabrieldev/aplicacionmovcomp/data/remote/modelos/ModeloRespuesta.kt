package com.gabrieldev.aplicacionmovcomp.data.remote.modelos

/**
 * Subcolección Firestore: preguntas/{idPregunta}/respuestas/{idRespuesta}
 */
data class ModeloRespuesta(
    val idPregunta: String = "",
    val textoOpcion: String = "",
    val esCorrecta: Boolean = false
)
