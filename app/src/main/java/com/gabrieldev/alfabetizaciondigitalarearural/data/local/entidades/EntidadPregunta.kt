package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "preguntas",
    foreignKeys = [
        ForeignKey(
            entity = EntidadCuestionario::class,
            parentColumns = ["id_cuestionario"],
            childColumns = ["id_cuestionario"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntidadPregunta(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_pregunta")
    val idPregunta: Int = 0,

    @ColumnInfo(name = "id_cuestionario")
    val idCuestionario: Int,

    @ColumnInfo(name = "enunciado")
    val enunciado: String
)
