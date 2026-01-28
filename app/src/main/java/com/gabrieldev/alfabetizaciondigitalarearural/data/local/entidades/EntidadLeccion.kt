package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecciones")
data class EntidadLeccion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_leccion")
    val idLeccion: Int = 0,

    @ColumnInfo(name = "uuid_global")
    val uuidGlobal: String,

    val titulo: String,

    val tema: String,

    @ColumnInfo(name = "autor_original")
    val autorOriginal: String,

    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: Long,

    @ColumnInfo(name = "creada_por_usuario")
    val creadaPorUsuario: Boolean,

    @ColumnInfo(name = "uuid_autor_original")
    val uuidAutorOriginal: String,

    @ColumnInfo(name = "imagen_url")
    val imagenUrl: String?,
)
