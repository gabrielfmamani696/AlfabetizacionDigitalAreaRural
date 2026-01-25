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
}