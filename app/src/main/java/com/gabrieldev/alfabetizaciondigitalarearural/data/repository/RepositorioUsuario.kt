package com.gabrieldev.alfabetizaciondigitalarearural.data.repository

import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.LeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.UsuarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import kotlinx.coroutines.flow.Flow
import java.util.UUID
//intermediario de datos
class RepositorioUsuario(
    private val usuarioDao: UsuarioDao,
    private val leccionDao: LeccionDao
    ) {
    // Obtener el usuario activo (para la pantalla principal)
    val ultimoUsuario: Flow<EntidadUsuario?> = usuarioDao.obtenerUltimoUsuario()
    // Crear un nuevo usuario
    suspend fun crearUsuario(nombre: String, avatarId: Int = 0) {
        val nuevoUsuario = EntidadUsuario(
            alias = nombre,
            ultimaActividad = System.currentTimeMillis(),
            uuidUsuario = UUID.randomUUID().toString()
        )
        usuarioDao.insertarUsuario(nuevoUsuario)
    }

    suspend fun existeAlgunUsuario(): Boolean {
        return usuarioDao.contarUsuarios() > 0
    }

    suspend fun obtenerLecciones(): List<EntidadLeccion> {
        return leccionDao.consultarLecciones()
    }

    suspend fun defaultLecciones() {
        val lecciones = obtenerLecciones()
        if(lecciones.isEmpty()){
            val leccion1 = EntidadLeccion(
                uuidGlobal = UUID.randomUUID().toString(),
                titulo = "Encender la Computadora",
                tema = "Hardware",
                autorOriginal = "Sistema",
                fechaCreacion = System.currentTimeMillis(),
                creadaPorUsuario = false,
                uuidAutorOriginal = "sistema",
                imagenUrl = "url_o_path_placeholder"
            )
            val leccion2 = EntidadLeccion(
                uuidGlobal = UUID.randomUUID().toString(),
                titulo = "El Ratón y el Teclado",
                tema = "Hardware",
                autorOriginal = "Sistema",
                fechaCreacion = System.currentTimeMillis(),
                creadaPorUsuario = false,
                uuidAutorOriginal = "sistema",
                imagenUrl = "url_o_path_placeholder"
            )
            leccionDao.insertarLeccion(leccion1)
            leccionDao.insertarLeccion(leccion2)
        }

    }
}