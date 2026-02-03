package com.gabrieldev.alfabetizaciondigitalarearural.data.remote

import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.CuestionarioConPreguntas

// para transportar la informacion entre aplicacion
data class LeccionTransferible(
    val versionProtocolo: Int = 1,
    val leccion: EntidadLeccion,
    val tarjetas: List<EntidadTarjeta>,
    val cuestionarios: List<CuestionarioConPreguntas>,
    val imagenPortadaBase64: String? = null,
    val imagenesTarjetasBase64: Map<Int, String> = emptyMap() // orden -> imagen en Base64
)
