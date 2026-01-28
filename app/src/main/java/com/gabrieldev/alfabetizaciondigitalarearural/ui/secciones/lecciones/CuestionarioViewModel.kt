package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.CuestionarioConPreguntas
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CuestionarioViewModel(
    private val repositorio: RepositorioApp,
    private val idLeccion: Int,
    private val idUsuario: Int
) : ViewModel() {
    // guarda el estado del cuestionario, respuesta y el indice de la pregunta actual
    private val _estadoCuestionario = MutableStateFlow<EstadoCuestionario>(EstadoCuestionario.Cargando)
    val estadoCuestionario = _estadoCuestionario.asStateFlow()

    private val _respuestasUsuario = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val respuestasUsuario = _respuestasUsuario.asStateFlow()

    private val _indicePreguntaActual = MutableStateFlow(0)
    val indicePreguntaActual = _indicePreguntaActual.asStateFlow()

    init {
        cargarCuestionario()
    }

    private fun cargarCuestionario() {
        viewModelScope.launch {
            val cuestionario = repositorio.obtenerCuestionarioAleatorio(idLeccion)
            if (cuestionario != null && cuestionario.preguntas.isNotEmpty()) {
                _estadoCuestionario.value = EstadoCuestionario.CuestionarioActivo(cuestionario)
            } else {
                _estadoCuestionario.value = EstadoCuestionario.Error("No hay cuestionario para esta lección")
            }
        }
    }

    fun seleccionarRespuesta(idPregunta: Int, idRespuesta: Int) {
        _respuestasUsuario.value = _respuestasUsuario.value.toMutableMap().apply {
            // PreguntaID -> RespuestaID
            put(idPregunta, idRespuesta)
        }
    }

    fun siguientePregunta() {
        val estado = _estadoCuestionario.value
        if (estado is EstadoCuestionario.CuestionarioActivo) {
            val totalPreguntas = estado.datos.preguntas.size
            if (_indicePreguntaActual.value < totalPreguntas - 1) {
                _indicePreguntaActual.value += 1
            }
        }
    }

    fun anteriorPregunta() {
        if (_indicePreguntaActual.value > 0) {
            _indicePreguntaActual.value -= 1
        }
    }

    fun finalizarCuestionario() {
        val estado = _estadoCuestionario.value
        if (estado is EstadoCuestionario.CuestionarioActivo) {
            viewModelScope.launch {
                val preguntas = estado.datos.preguntas
                var puntajeTotal = 0
                val totalPreguntas = preguntas.size
                // recorrido
                preguntas.forEach { p ->
                    val respuestaUsuarioId = _respuestasUsuario.value[p.pregunta.idPregunta]
                    val respuestaCorrecta = p.respuestas.find { it.esCorrecta }
                    if (respuestaUsuarioId == respuestaCorrecta?.idRespuesta) {
                        puntajeTotal++
                    }
                }

                val calificacionFinal = if (totalPreguntas > 0) (puntajeTotal * 100) / totalPreguntas else 0
                val aprobacion = calificacionFinal >= 60

                val intento = EntidadIntentoLeccion(
                    idUsuario = idUsuario,
                    idLeccion = idLeccion,
                    calificacionObtenida = calificacionFinal,
                    uuidIntento = UUID.randomUUID().toString(),
                    completadoExitosamente = aprobacion
                )
                repositorio.insertarIntento(intento)

                _estadoCuestionario.value = EstadoCuestionario.Finalizado(calificacionFinal, aprobacion)
            }
        }
    }
}

sealed class EstadoCuestionario {
    //Estados de cuestionario definidos en base a
    // la presencia de los atributos del objeto CuestionarioConPreguntas
    object Cargando : EstadoCuestionario()
    data class CuestionarioActivo(val datos: CuestionarioConPreguntas) : EstadoCuestionario()
    data class Finalizado(val nota: Int, val aprobado: Boolean) : EstadoCuestionario()
    data class Error(val mensaje: String) : EstadoCuestionario()
}

class CuestionarioViewModelFactory(
    private val repositorio: RepositorioApp,
    private val idLeccion: Int,
    private val idUsuario: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CuestionarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CuestionarioViewModel(repositorio, idLeccion, idUsuario) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
