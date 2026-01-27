package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tarjetas",
    foreignKeys = [
        ForeignKey(
            entity = EntidadLeccion::class,
            parentColumns = ["id_leccion"],
            childColumns = ["id_leccion"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntidadTarjeta(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_tarjeta")
    val idTarjeta: Int = 0,

    @ColumnInfo(name = "id_leccion")
    val idLeccion: Int,

    @ColumnInfo(name = "orden_secuencia")
    val ordenSecuencia: Int,

    @ColumnInfo(name = "contenido_texto")
    val contenidoTexto: String,

    @ColumnInfo(name = "tipo_fondo")
    val tipoFondo: String, // "COLOR_SOLIDO", "SVG", o "IMAGEN"

    @ColumnInfo(name = "data_fondo")
    val dataFondo: String // Hex del color, Path del SVG, o nombre del drawable
)
