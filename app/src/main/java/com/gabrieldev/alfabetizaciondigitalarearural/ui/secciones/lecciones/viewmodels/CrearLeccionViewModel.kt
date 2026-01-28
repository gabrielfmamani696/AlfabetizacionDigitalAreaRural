package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadCuestionario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.PreguntaConRespuestas
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class CrearLeccionViewModel(
    private val repositorio: RepositorioApp
): ViewModel() {
    //estado de flujo mutable en el tiepo, acceso de escritura y lectura de variable _titulo
    private val _titulo = MutableStateFlow("")
    //estado de flujo, solo acceso de lectura
    val titulo = _titulo.asStateFlow()

    private val _tema = MutableStateFlow("")
    val tema = _tema.asStateFlow()

    private val _listaTarjetas = MutableStateFlow<List<EntidadTarjeta>>(emptyList())
    private val listaTarjetas = _listaTarjetas.asStateFlow()

    private val _mensajeUsuario = MutableStateFlow<String?>(null)
    private val mensajeUsuario = _mensajeUsuario.asStateFlow()

    private val _navegarAtras = MutableStateFlow(false)
    private val navegarAtras = _navegarAtras.asStateFlow()

    private val _listaPreguntas = MutableStateFlow<List<PreguntaConRespuestas>>(emptyList())
    private val listaPreguntas = _listaPreguntas.asStateFlow()

    //escritura del usuario
    fun actualizarTitulo(nuevoTitulo: String) {
        _titulo.value = nuevoTitulo
    }

    fun actualizarTema(nuevoTema: String) {
        _tema.value = nuevoTema
    }

    fun agregarTarjeta(
        contenido: String,
        tipoFondo: String,
        dataFondo: String
    ) {
        val nuevaTarjeta = EntidadTarjeta(
            idLeccion = 0,
            ordenSecuencia = _listaTarjetas.value.size + 1,
            contenidoTexto = contenido,
            tipoFondo = tipoFondo,
            dataFondo = dataFondo
        )

        // anadimos la nueva tarjeta a la lista _listaTarjetas.value
        _listaTarjetas.value = _listaTarjetas.value + nuevaTarjeta
    }

    fun eliminarTarjeta(index: Int) {
        val listaMutable = _listaTarjetas.value.toMutableList()
        if (index in listaMutable.indices) {
            listaMutable.removeAt(index)

            // Re-ordenamos para que no queden vacios entre tarjetas
            listaMutable.forEachIndexed { i, tarjeta ->
                listaMutable[i] = tarjeta.copy(ordenSecuencia = i + 1)
            }
            _listaTarjetas.value = listaMutable
        }
    }

    fun guardarLeccion() {

        if (_titulo.value.isBlank() || _tema.value.isBlank()) {
            _mensajeUsuario.value = "Por favor completa el título y tema"
            return
        }

        if (_listaTarjetas.value.isEmpty()) {
            _mensajeUsuario.value = "Debe haber al menos una tarjeta"
            return
        }

        viewModelScope.launch {
            try {
                val usuario = repositorio.ultimoUsuario.firstOrNull()

                val alias = usuario?.alias ?: "Desconocido"
                val uuid = usuario?.uuidUsuario ?: "local"

                val nuevaLeccion = EntidadLeccion(
                    titulo = _titulo.value,
                    tema = _tema.value,
                    autorOriginal = alias,
                    uuidGlobal = UUID.randomUUID().toString(),
                    fechaCreacion = System.currentTimeMillis(),
                    creadaPorUsuario = true,
                    uuidAutorOriginal = uuid,
                    // TODO insertar la url de donde se encuentra la imagen
                    imagenUrl = null
                )

                val idLeccionGenerado = repositorio.insertarLeccion(nuevaLeccion)

                val tarjetasFinales = _listaTarjetas.value.map { tarjeta ->
                    tarjeta.copy(idLeccion = idLeccionGenerado.toInt())
                }

                tarjetasFinales.forEach { repo ->
                    repositorio.insertarTarjeta(repo)
                }

                if(_listaPreguntas.value.isNotEmpty()){
                    val nuevoCuestionario: EntidadCuestionario = EntidadCuestionario(
                        idLeccion = idLeccionGenerado.toInt(),
                        tituloQuiz = "Cuestionario: ${nuevaLeccion.titulo}"
                    )

                    repositorio.insertarCuestionarioCompleto(
                        cuestionario = nuevoCuestionario,
                        preguntas = _listaPreguntas.value
                    )
                }

                _mensajeUsuario.value = "Lección creada con éxito"
                _navegarAtras.value = true

            } catch (e: Exception) {
                _mensajeUsuario.value = "Error al guardar: ${e.localizedMessage}"
            }
        }
    }

    fun limpiarMensajes() {
        _mensajeUsuario.value = null
    }

    fun agregarPregunta(
        enunciado: String,
        respuestas: List<EntidadRespuesta>
        ) {

        val nuevaPregunta: EntidadPregunta = EntidadPregunta(
            idCuestionario = 0,
            enunciado = enunciado
        )

        val preguntaConRespuestas: PreguntaConRespuestas = PreguntaConRespuestas(nuevaPregunta, respuestas)

        _listaPreguntas.value = _listaPreguntas.value + preguntaConRespuestas
    }

    fun eliminarPregunta(index: Int) {
        val listaMutable = _listaPreguntas.value.toMutableList()
        if (index in listaMutable.indices) {
            listaMutable.removeAt(index)
            _listaPreguntas.value = listaMutable
        }
    }


    // Segundo Constructor, Android lo maneja
    class CrearLeccionViewModelFactory(
        private val repositorio: RepositorioApp
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CrearLeccionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CrearLeccionViewModel(repositorio) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}