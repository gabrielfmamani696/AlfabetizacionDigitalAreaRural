package com.gabrieldev.aplicacionmovcomp.data.remote

import com.gabrieldev.aplicacionmovcomp.data.remote.modelos.ModeloUsuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Fuente de datos para la colección 'usuarios' en Firestore.
 * El ID del documento es el uuid_usuario generado localmente.
 */
class FirestoreUsuarioFuente {

    //peticion a la base de datos en Firestore
    private val coleccion = FirebaseFirestore.getInstance().collection("usuarios")

    suspend fun obtenerUsuario(uuidUsuario: String): ModeloUsuario? {
        return try {
            val doc = coleccion.document(uuidUsuario).get().await()
            if (doc.exists()) doc.toObject(ModeloUsuario::class.java) else null
        } catch (e: Exception) { null }
    }

    suspend fun guardarUsuario(
        uuidUsuario: String,
        modelo: ModeloUsuario
    ) {
        coleccion.document(uuidUsuario).set(modelo).await()
    }

    suspend fun existeUsuario(uuidUsuario: String): Boolean {
        return try {
            coleccion.document(uuidUsuario).get().await().exists()
        } catch (e: Exception) { false }
    }

    suspend fun actualizarProgreso(
        uuidUsuario: String,
        puntosTotales: Int,
        rachaDias: Int,
        ultimaActividad: Long
    ) {
        coleccion.document(uuidUsuario).update(
            mapOf(
                "puntosTotales" to puntosTotales,
                "rachaActualDias" to rachaDias,
                "ultimaActividad" to ultimaActividad
            )
        ).await()
    }

    suspend fun actualizarAvatar(
        uuidUsuario: String,
        idAvatar: String
    ) {
        coleccion.document(uuidUsuario).update("idAvatar", idAvatar).await()
    }

    suspend fun actualizarAlias(
        uuidUsuario: String,
        nuevoAlias: String
    ) {
        coleccion.document(uuidUsuario).update("alias", nuevoAlias).await()
    }

    suspend fun eliminarUsuario(uuidUsuario: String) {
        coleccion.document(uuidUsuario).delete().await()
    }

    suspend fun guardarLogroNotificado(
        uuidUsuario: String,
        tipoLogroNombre: String,
        fecha: Long
    ) {
        coleccion.document(uuidUsuario).collection("logros_notificados")
            .document(tipoLogroNombre)
            .set(mapOf(
                "tipoLogro" to tipoLogroNombre,
                "fechaDesbloqueo" to fecha
            )).await()
    }
}

