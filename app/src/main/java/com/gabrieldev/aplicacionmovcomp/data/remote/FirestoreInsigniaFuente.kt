package com.gabrieldev.aplicacionmovcomp.data.remote

import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloInsignia
import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloLogroNotificado
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreInsigniaFuente {

    private val db = FirebaseFirestore.getInstance()
    private val insignias = db.collection("insignias")
    private val usuarios = db.collection("usuarios")



    suspend fun obtenerTodasLasInsignias(): List<Pair<String, ModeloInsignia>> {
        return try {
            insignias.get().await().documents.mapNotNull { doc ->
                val modelo = doc.toObject(ModeloInsignia::class.java)
                if (modelo != null) Pair(doc.id, modelo) else null
            }
        } catch (e: Exception) { emptyList() }
    }



    suspend fun obtenerLogrosDeUsuario(uuidUsuario: String): List<ModeloLogroNotificado> {
        return try {
            usuarios.document(uuidUsuario)
                .collection("logros_notificados").get().await()
                .documents.mapNotNull { it.toObject(ModeloLogroNotificado::class.java) }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun logroYaNotificado(uuidUsuario: String, idInsignia: String): Boolean {
        return try {
            val result = usuarios.document(uuidUsuario)
                .collection("logros_notificados")
                .whereEqualTo("idInsignia", idInsignia).get().await()
            !result.isEmpty
        } catch (e: Exception) { false }
    }

    suspend fun marcarLogroComoNotificado(uuidUsuario: String, modelo: ModeloLogroNotificado) {
        usuarios.document(uuidUsuario)
            .collection("logros_notificados")
            .add(modelo).await()
    }
}
