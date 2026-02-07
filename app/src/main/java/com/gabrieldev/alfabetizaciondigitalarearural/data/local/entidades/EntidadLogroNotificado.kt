package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "logros_notificados",
    foreignKeys = [
        ForeignKey(
            entity = EntidadUsuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id_usuario")
    ]
)
data class EntidadLogroNotificado(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "tipo_logro")
    val tipoLogro: String,

    @ColumnInfo(name = "fecha_notificacion")
    val fechaNotificacion: Long = System.currentTimeMillis()
)