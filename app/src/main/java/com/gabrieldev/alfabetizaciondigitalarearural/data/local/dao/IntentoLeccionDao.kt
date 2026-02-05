package com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion

@Dao
interface IntentoLeccionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIntento(intento: EntidadIntentoLeccion)

    @Query("SELECT * FROM intentos_leccion WHERE id_usuario = :idUsuario ORDER BY fecha_intento DESC")
    suspend fun obtenerIntentosPorUsuario(idUsuario: Int): List<EntidadIntentoLeccion>

    @Query("SELECT * FROM intentos_leccion WHERE id_usuario = :idUsuario AND id_leccion = :idLeccion ORDER BY calificacion_obtenida DESC LIMIT 1")
    suspend fun obtenerMejorIntento(idUsuario: Int, idLeccion: Int): EntidadIntentoLeccion?

    @Query(
        "SELECT AVG(calificacion_obtenida) " +
            "FROM intentos_leccion " +
            "WHERE id_usuario = :idUsuario " +
            "AND id_leccion = :idLeccion")
    suspend fun obtenerPromedioPorLeccionEspecifica(idUsuario: Int, idLeccion: Int): Double?

    @Query(
        "SELECT DISTINCT l.* " +
                "FROM lecciones l, intentos_leccion i " +
                "WHERE i.id_usuario = :idUsuario " +
                "AND i.id_leccion = l.id_leccion "
    )
    suspend fun obtenerLeccionesRealizadasPorUsuario(idUsuario: Int): List<EntidadLeccion>
}
