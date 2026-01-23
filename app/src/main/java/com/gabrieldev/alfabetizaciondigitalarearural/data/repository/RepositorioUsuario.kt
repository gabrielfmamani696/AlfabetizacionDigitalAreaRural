package com.gabrieldev.alfabetizaciondigitalarearural.data.repository

import com.gabrieldev.alfabetizaciondigitalarearural.data.local.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.UsuarioDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class RepositorioUsuario(private val usuarioDao: UsuarioDao) {

    // Obtener el usuario activo (para la pantalla principal)
    val ultimoUsuario: Flow<EntidadUsuario?> = usuarioDao.obtenerUltimoUsuario()

    // Crear un nuevo usuario (para el Onboarding)
    suspend fun crearUsuario(nombre: String, avatarId: Int = 0) {
        val nuevoUsuario = EntidadUsuario(
            alias = nombre,
            ultimaActividad = System.currentTimeMillis(),
            uuidUsuario = UUID.randomUUID().toString()
        )
        usuarioDao.insertarUsuario(nuevoUsuario)
    }

    // Verificar si ya existe alguien (para decidir si mostrar Onboarding o Home)
    suspend fun existeAlgunUsuario(): Boolean {
        return usuarioDao.contarUsuarios() > 0
    }
}