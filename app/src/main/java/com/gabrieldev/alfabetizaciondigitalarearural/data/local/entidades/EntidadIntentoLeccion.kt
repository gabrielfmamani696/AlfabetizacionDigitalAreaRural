package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "intentos_leccion",
    foreignKeys = [
        ForeignKey(
            entity = EntidadUsuario::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE   // Si se borra al usuario se borra al intento
        ),
        ForeignKey(
            entity = EntidadLeccion::class,
            parentColumns = ["id_leccion"],
            childColumns = ["id_leccion"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EntidadIntentoLeccion (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_intento")
    val idIntento: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    @ColumnInfo(name = "id_leccion")
    val idLeccion: Int,

    @ColumnInfo(name = "calificacion_obtenida")
    val calificacionObtenida: Int,

    @ColumnInfo(name = "fecha_intento")
    val fechaIntento: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "uuid_intento")
    val uuidIntento: String,

    @ColumnInfo(name = "completado_exitosamente")
    val completadoExitosamente: Boolean,

)

