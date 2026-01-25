package com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion

@Dao
interface LeccionDao {

    //crear leccion
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertarLeccion(leccion: EntidadLeccion)

    //consultar/leer lecciones
    @Query("SELECT * FROM lecciones ORDER BY fecha_creacion")
    suspend fun consultarLecciones(): List<EntidadLeccion>

    //borra una leccion en especifico
    @Query("DELETE FROM lecciones WHERE id_leccion = :id")
    suspend fun eliminarLeccionPorId(id: Int)

    //editar una leccion
    @Update
    suspend fun actualizarLeccion(leccion: EntidadLeccion)
}