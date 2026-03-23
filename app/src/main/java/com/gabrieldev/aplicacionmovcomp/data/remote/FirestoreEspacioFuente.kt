package com.gabrieldev.aplicacionmovcomp.data.remote

import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloEspacioAprendizaje
import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloIntentoLeccion
import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloMiembroEspacio
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreEspacioFuente {

    private val db = FirebaseFirestore.getInstance()
    private val espacios = db.collection("espacios_aprendizaje")
    private val miembros = db.collection("miembros_espacio")
    private val intentos = db.collection("intentos_leccion")



    suspend fun guardarEspacio(idEspacio: String, modelo: ModeloEspacioAprendizaje) {
        espacios.document(idEspacio).set(modelo).await()
    }

    suspend fun obtenerEspaciosDeDocente(uuidDocente: String): List<Pair<String, ModeloEspacioAprendizaje>> {
        return try {
            espacios.whereEqualTo("idDocenteCreador", uuidDocente).get().await()
                .documents.mapNotNull { doc ->
                    val modelo = doc.toObject(ModeloEspacioAprendizaje::class.java)
                    if (modelo != null) Pair(doc.id, modelo) else null
                }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerEspacioPorToken(token: String): Pair<String, ModeloEspacioAprendizaje>? {
        return try {
            val result = espacios.whereEqualTo("tokenAcceso", token).get().await()
            val doc = result.documents.firstOrNull() ?: return null
            val modelo = doc.toObject(ModeloEspacioAprendizaje::class.java) ?: return null
            Pair(doc.id, modelo)
        } catch (e: Exception) { null }
    }



    suspend fun inscribirMiembro(modelo: ModeloMiembroEspacio) {
        val idDoc = "${modelo.idEspacio}_${modelo.idEstudiante}"
        miembros.document(idDoc).set(modelo).await()
    }

    suspend fun obtenerEspaciosDeEstudiante(uuidEstudiante: String): List<String> {
        return try {
            miembros.whereEqualTo("idEstudiante", uuidEstudiante).get().await()
                .documents.mapNotNull { it.toObject(ModeloMiembroEspacio::class.java)?.idEspacio }
        } catch (e: Exception) { emptyList() }
    }



    suspend fun guardarIntento(modelo: ModeloIntentoLeccion) {
        intentos.add(modelo).await()
    }

    suspend fun obtenerIntentosDeUsuario(uuidUsuario: String): List<ModeloIntentoLeccion> {
        return try {
            intentos.whereEqualTo("idUsuario", uuidUsuario).get().await()
                .documents.mapNotNull { it.toObject(ModeloIntentoLeccion::class.java) }
        } catch (e: Exception) { emptyList() }
    }
}
