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

    @ColumnInfo(name = "vio_onboarding_consumo")
    val vioOnboardingConsumo: Boolean = false,

    @ColumnInfo(name = "vio_onboarding_creacion")
    val vioOnboardingCreacion: Boolean = false,

    @ColumnInfo(name = "uuid_usuario")
    val uuidUsuario: String
)