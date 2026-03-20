package com.gabrieldev.aplicacionmovcomp.data.remote

import com.gabrieldev.aplicacionmovcomp.data.local.entidades.EntidadLeccion
import com.gabrieldev.aplicacionmovcomp.data.local.entidades.EntidadTarjeta
import com.gabrieldev.aplicacionmovcomp.data.repository.CuestionarioConPreguntas

// para transportar la informacion entre aplicacion
data class LeccionTransferible(
    val versionProtocolo: Int = 1,
    val leccion: EntidadLeccion,
    val tarjetas: List<EntidadTarjeta>,
    val cuestionarios: List<CuestionarioConPreguntas>,
    val imagenPortadaBase64: String? = null,
    val imagenesTarjetasBase64: Map<Int, String> = emptyMap() // orden -> imagen en Base64
)
