package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "respuestas",
    foreignKeys = [
        ForeignKey(
            entity = EntidadPregunta::class,
            parentColumns = ["id_pregunta"],
            childColumns = ["id_pregunta"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntidadRespuesta(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_respuesta")
    val idRespuesta: Int = 0,

    @ColumnInfo(name = "id_pregunta")
    val idPregunta: Int,

    @ColumnInfo(name = "texto_opcion")
    val textoOpcion: String,

    @ColumnInfo(name = "es_correcta")
    val esCorrecta: Boolean
)
