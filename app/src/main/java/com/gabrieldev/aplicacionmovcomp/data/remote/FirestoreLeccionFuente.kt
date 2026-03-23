package com.gabrieldev.aplicacionmovcomp.data.remote

import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloCuestionario
import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloLeccion
import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloPregunta
import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloRespuesta
import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloTarjeta
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreLeccionFuente {

    private val db = FirebaseFirestore.getInstance()
    private val lecciones = db.collection("lecciones")


    suspend fun obtenerTodasLasLecciones(): List<Pair<String, ModeloLeccion>> {
        return try {
            lecciones.get().await().documents.mapNotNull { doc ->
                val modelo = doc.toObject(ModeloLeccion::class.java)
                if (modelo != null) Pair(doc.id, modelo) else null
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerLeccion(idLeccion: String): ModeloLeccion? {
        return try {
            val doc = lecciones.document(idLeccion).get().await()
            if (doc.exists()) doc.toObject(ModeloLeccion::class.java) else null
        } catch (e: Exception) { null }
    }

    suspend fun guardarLeccion(idLeccion: String, modelo: ModeloLeccion) {
        lecciones.document(idLeccion).set(modelo).await()
    }

    suspend fun eliminarLeccion(idLeccion: String) {
        lecciones.document(idLeccion).delete().await()
    }



    suspend fun obtenerTarjetas(idLeccion: String): List<Pair<String, ModeloTarjeta>> {
        return try {
            lecciones.document(idLeccion).collection("tarjetas")
                .orderBy("ordenSecuencia").get().await()
                .documents.mapNotNull { doc ->
                    val modelo = doc.toObject(ModeloTarjeta::class.java)
                    if (modelo != null) Pair(doc.id, modelo) else null
                }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun guardarTarjeta(idLeccion: String, idTarjeta: String, modelo: ModeloTarjeta) {
        lecciones.document(idLeccion).collection("tarjetas")
            .document(idTarjeta).set(modelo).await()
    }



    suspend fun obtenerCuestionarios(idLeccion: String): List<Pair<String, ModeloCuestionario>> {
        return try {
            lecciones.document(idLeccion).collection("cuestionarios").get().await()
                .documents.mapNotNull { doc ->
                    val modelo = doc.toObject(ModeloCuestionario::class.java)
                    if (modelo != null) Pair(doc.id, modelo) else null
                }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun guardarCuestionario(idLeccion: String, idCuestionario: String, modelo: ModeloCuestionario) {
        lecciones.document(idLeccion).collection("cuestionarios")
            .document(idCuestionario).set(modelo).await()
    }



    suspend fun obtenerPreguntas(idLeccion: String, idCuestionario: String): List<Pair<String, ModeloPregunta>> {
        return try {
            lecciones.document(idLeccion).collection("cuestionarios")
                .document(idCuestionario).collection("preguntas").get().await()
                .documents.mapNotNull { doc ->
                    val modelo = doc.toObject(ModeloPregunta::class.java)
                    if (modelo != null) Pair(doc.id, modelo) else null
                }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun guardarPregunta(idLeccion: String, idCuestionario: String, idPregunta: String, modelo: ModeloPregunta) {
        lecciones.document(idLeccion).collection("cuestionarios")
            .document(idCuestionario).collection("preguntas")
            .document(idPregunta).set(modelo).await()
    }



    suspend fun obtenerRespuestas(idLeccion: String, idCuestionario: String, idPregunta: String): List<Pair<String, ModeloRespuesta>> {
        return try {
            lecciones.document(idLeccion).collection("cuestionarios")
                .document(idCuestionario).collection("preguntas")
                .document(idPregunta).collection("respuestas").get().await()
                .documents.mapNotNull { doc ->
                    val modelo = doc.toObject(ModeloRespuesta::class.java)
                    if (modelo != null) Pair(doc.id, modelo) else null
                }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun guardarRespuesta(idLeccion: String, idCuestionario: String, idPregunta: String, idRespuesta: String, modelo: ModeloRespuesta) {
        lecciones.document(idLeccion).collection("cuestionarios")
            .document(idCuestionario).collection("preguntas")
            .document(idPregunta).collection("respuestas")
            .document(idRespuesta).set(modelo).await()
    }
}
