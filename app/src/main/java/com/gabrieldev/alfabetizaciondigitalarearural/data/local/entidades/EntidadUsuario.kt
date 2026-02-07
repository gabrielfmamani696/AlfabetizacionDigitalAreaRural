package com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class EntidadUsuario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    @ColumnInfo(name = "alias")
    val alias: String,

    @ColumnInfo(name = "puntos_totales")
    val puntosTotales: Int = 0,

    @ColumnInfo(name = "racha_actual_dias")
    val rachaActualDias: Int = 0,

    @ColumnInfo(name = "ultima_actividad")
    val ultimaActividad: Long,

    @ColumnInfo(name = "uuid_usuario")
    val uuidUsuario: String,

    @ColumnInfo(name = "activo")
    val activo: Boolean = false,

    @ColumnInfo(name = "notificaciones_habilitadas")
    val notificacionesHabilitadas: Boolean = true,

    @ColumnInfo(name = "hora_notificacion")
    val horaNotificacion: Int = 18,

    @ColumnInfo(name = "minuto_notificacion")
    val minutoNotificacion: Int = 0,
)