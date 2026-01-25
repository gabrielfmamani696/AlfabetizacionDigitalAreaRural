package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cuestionarios",
    foreignKeys = [
        ForeignKey(
            entity = EntidadLeccion::class,
            parentColumns = ["id_leccion"],
            childColumns = ["id_leccion"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntidadCuestionario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_cuestionario")
    val idCuestionario: Int = 0,

    @ColumnInfo(name = "id_leccion")
    val idLeccion: Int,

    @ColumnInfo(name = "titulo_quiz")
    val tituloQuiz: String
)
