package com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion

@Dao
interface IntentoLeccionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIntento(intento: EntidadIntentoLeccion)

    @Query("SELECT * FROM intentos_leccion WHERE id_usuario = :idUsuario ORDER BY fecha_intento DESC")
    suspend fun obtenerIntentosPorUsuario(idUsuario: Int): List<EntidadIntentoLeccion>

    @Query("SELECT * FROM intentos_leccion WHERE id_usuario = :idUsuario AND id_leccion = :idLeccion ORDER BY calificacion_obtenida DESC LIMIT 1")
    suspend fun obtenerMejorIntento(idUsuario: Int, idLeccion: Int): EntidadIntentoLeccion?

    @Query("SELECT AVG(calificacion_obtenida) FROM intentos_leccion WHERE id_usuario = :idUsuario")
    suspend fun obtenerPromedioCalificaciones(idUsuario: Int): Double?
}
