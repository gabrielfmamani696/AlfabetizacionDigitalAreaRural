package com.gabrieldev.alfabetizaciondigitalarearural.data.remote

import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.CuestionarioConPreguntas

data class LeccionTransferible(
    val versionProtocolo: Int = 1,
    val leccion: EntidadLeccion,
    val tarjetas: List<EntidadTarjeta>,
    val cuestionarios: List<CuestionarioConPreguntas>
)
