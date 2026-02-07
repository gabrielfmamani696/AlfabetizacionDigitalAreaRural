package com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLogroNotificado

@Dao
interface LogroNotificadoDao {

    @Insert
    suspend fun marcarComoNotificado(logroNotificado: EntidadLogroNotificado)

    @Query("SELECT COUNT(*) > 0" +
            " FROM logros_notificados " +
            " WHERE id_usuario = :idUsuario " +
            " AND tipo_logro = :tipoLogro")
    suspend fun fueNotificado(idUsuario: Int, tipoLogro: String): Boolean

    @Query("SELECT *" +
            "FROM logros_notificados " +
            "WHERE id_usuario = :idUsuario")
    suspend fun obtenerLogrosNotificados(idUsuario: Int): List<EntidadLogroNotificado>

    @Query("DELETE FROM logros_notificados " +
            "WHERE id_usuario = :idUsuario")
    suspend fun resetearNotificaciones(idUsuario: Int)
}