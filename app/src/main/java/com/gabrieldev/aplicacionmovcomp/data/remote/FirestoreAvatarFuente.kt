package com.gabrieldev.aplicacionmovcomp.data.remote

import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloOpcionAvatar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreAvatarFuente {

    private val avatares = FirebaseFirestore.getInstance().collection("opciones_avatar")

    suspend fun obtenerTodosLosAvatares(): List<Pair<String, ModeloOpcionAvatar>> {
        return try {
            avatares.get().await().documents.mapNotNull { doc ->
                val modelo = doc.toObject(ModeloOpcionAvatar::class.java)
                if (modelo != null) Pair(doc.id, modelo) else null
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun obtenerAvatar(idAvatar: String): ModeloOpcionAvatar? {
        return try {
            val doc = avatares.document(idAvatar).get().await()
            if (doc.exists()) doc.toObject(ModeloOpcionAvatar::class.java) else null
        } catch (e: Exception) { null }
    }
}
