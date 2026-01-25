package com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta

@Dao
interface TarjetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarjeta(tarjeta: EntidadTarjeta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarjetas(tarjetas: List<EntidadTarjeta>)

    @Query("SELECT * FROM tarjetas WHERE id_leccion = :leccionId ORDER BY orden_secuencia ASC")
    suspend fun obtenerTarjetasPorLeccion(leccionId: Int): List<EntidadTarjeta>
}
