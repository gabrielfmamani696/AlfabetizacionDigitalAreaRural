package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.ExamenConPreguntas
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ExamenViewModel(
    private val repositorio: RepositorioUsuario,
    private val idLeccion: Int,
    private val idUsuario: Int
) : ViewModel() {
    // guarda el estado del examen, respuesta y el indice de la pregunta actual
    private val _estadoExamen = MutableStateFlow<EstadoExamen>(EstadoExamen.Cargando)
    val estadoExamen = _estadoExamen.asStateFlow()

    private val _respuestasUsuario = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val respuestasUsuario = _respuestasUsuario.asStateFlow()

    private val _indicePreguntaActual = MutableStateFlow(0)
    val indicePreguntaActual = _indicePreguntaActual.asStateFlow()

    init {
        cargarExamen()
    }

    private fun cargarExamen() {
        viewModelScope.launch {
            val examen = repositorio.obtenerCuestionarioAleatorio(idLeccion)
            if (examen != null && examen.preguntas.isNotEmpty()) {
                _estadoExamen.value = EstadoExamen.ExamenActivo(examen)
            } else {
                _estadoExamen.value = EstadoExamen.Error("No hay examen para esta lección")
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
        val estado = _estadoExamen.value
        if (estado is EstadoExamen.ExamenActivo) {
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

    fun finalizarExamen() {
        val estado = _estadoExamen.value
        if (estado is EstadoExamen.ExamenActivo) {
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

                _estadoExamen.value = EstadoExamen.Finalizado(calificacionFinal, aprobacion)
            }
        }
    }
}

sealed class EstadoExamen {
    //Estados de examen definidos en base a
    // la presencia de los atributos del objeto ExamenConPreguntas
    object Cargando : EstadoExamen()
    data class ExamenActivo(val datos: ExamenConPreguntas) : EstadoExamen()
    data class Finalizado(val nota: Int, val aprobado: Boolean) : EstadoExamen()
    data class Error(val mensaje: String) : EstadoExamen()
}

class ExamenViewModelFactory(
    private val repositorio: RepositorioUsuario,
    private val idLeccion: Int,
    private val idUsuario: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExamenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExamenViewModel(repositorio, idLeccion, idUsuario) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
