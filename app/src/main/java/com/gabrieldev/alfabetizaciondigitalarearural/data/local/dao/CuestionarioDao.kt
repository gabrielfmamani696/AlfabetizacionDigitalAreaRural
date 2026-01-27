package com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadCuestionario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta

@Dao
interface CuestionarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCuestionario(cuestionario: EntidadCuestionario): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPregunta(pregunta: EntidadPregunta): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRespuesta(respuesta: EntidadRespuesta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRespuestas(respuestas: List<EntidadRespuesta>)

    @Query("SELECT * FROM cuestionarios WHERE id_leccion = :leccionId")
    suspend fun obtenerCuestionariosPorLeccion(leccionId: Int): List<EntidadCuestionario>

    @Query("SELECT * FROM preguntas WHERE id_cuestionario = :cuestionarioId")
    suspend fun obtenerPreguntasPorCuestionario(cuestionarioId: Int): List<EntidadPregunta>

    @Query("SELECT * FROM respuestas WHERE id_pregunta = :preguntaId")
    suspend fun obtenerRespuestasPorPregunta(preguntaId: Int): List<EntidadRespuesta>
}
