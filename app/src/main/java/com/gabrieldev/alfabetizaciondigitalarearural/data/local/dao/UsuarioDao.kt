package com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
//    suspend = segudo plano/corrutina
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertarUsuario(usuario: EntidadUsuario)

    @Query("SELECT * FROM usuarios WHERE id_usuario = :id")
    suspend fun obtenerUsuarioPorId(id: Int): EntidadUsuario?

    @Query("SELECT * FROM usuarios ORDER BY ultima_actividad DESC LIMIT 1")
    fun obtenerUltimoUsuario(): Flow<EntidadUsuario?>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Query("SELECT * FROM usuarios WHERE activo = 1 LIMIT 1")
    fun obtenerUsuarioActivo(): Flow<EntidadUsuario?>

    @Query("UPDATE usuarios SET activo = 0")
    suspend fun desactivarTodosLosUsuarios()

    @Query("UPDATE usuarios SET activo = 1 WHERE id_usuario = :idUsuario")
    suspend fun activarUsuario(idUsuario: Int)

    @Query("SELECT * FROM usuarios ORDER BY ultima_actividad DESC")
    suspend fun obtenerTodosLosUsuarios(): List<EntidadUsuario>

    @Query("""
    UPDATE usuarios 
    SET notificaciones_habilitadas = :habilitadas,
        hora_notificacion = :hora,
        minuto_notificacion = :minuto
    WHERE id_usuario = :idUsuario
    """)
    suspend fun actualizarConfiguracionNotificaciones(
        idUsuario: Int,
        habilitadas: Boolean,
        hora: Int,
        minuto: Int
    )

    @Query("UPDATE usuarios SET ultima_actividad = :timestamp WHERE id_usuario = :idUsuario")
    suspend fun actualizarUltimaActividad(idUsuario: Int, timestamp: Long)
}