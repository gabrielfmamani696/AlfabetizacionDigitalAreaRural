package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadCuestionario
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

//mantiene el estado de los datos pese a cambios en la pantalla
class CrearLeccionViewModel(
    private val repositorio: RepositorioApp
): ViewModel() {
    //estado de flujo mutable en el tiepo, acceso de escritura y lectura de variable _titulo
    private val _titulo = MutableStateFlow("")
    //estado de flujo, solo acceso de lectura
    val titulo = _titulo.asStateFlow()

    private val _tema = MutableStateFlow("")
    val tema = _tema.asStateFlow()

    private val _theme = MutableStateFlow("") // Alias temporal
    
    // ESTRUCTURA PARA MANEJAR MÚLTIPLES CUESTIONARIOS
    data class CuestionarioBorrador(
        val idTemporal: String = UUID.randomUUID().toString(),
        val titulo: String,
        val preguntas: List<PreguntaConRespuestas> = emptyList()
    )

    // LISTA DE CUESTIONARIOS (En lugar de un solo cuestionario)
    private val _listaCuestionarios = MutableStateFlow<List<CuestionarioBorrador>>(emptyList())
    val listaCuestionarios = _listaCuestionarios.asStateFlow()

    // CUESTIONARIO SELECCIONADO PARA EDITAR (Si es null, mostramos la lista)
    private val _cuestionarioActivoId = MutableStateFlow<String?>(null)
    val cuestionarioActivoId = _cuestionarioActivoId.asStateFlow()

    fun seleccionarCuestionario(id: String?) {
        _cuestionarioActivoId.value = id
    }

    fun crearNuevoCuestionario(titulo: String) {
        val nuevo = CuestionarioBorrador(titulo = titulo)
        _listaCuestionarios.value = _listaCuestionarios.value + nuevo
        _cuestionarioActivoId.value = nuevo.idTemporal
    }

    fun eliminarCuestionario(id: String) {
        _listaCuestionarios.value = _listaCuestionarios.value.filter { it.idTemporal != id }
        if (_cuestionarioActivoId.value == id) {
            _cuestionarioActivoId.value = null
        }
    }

    private val _listaTarjetas = MutableStateFlow<List<EntidadTarjeta>>(emptyList())
    val listaTarjetas = _listaTarjetas.asStateFlow()

    private val _mensajeUsuario = MutableStateFlow<String?>(null)
    val mensajeUsuario = _mensajeUsuario.asStateFlow()

    private val _navegarAtras = MutableStateFlow(false)
    val navegarAtras = _navegarAtras.asStateFlow()

    fun actualizarTitulo(nuevoTitulo: String) {
        _titulo.value = nuevoTitulo
    }

    fun actualizarTema(nuevoTema: String) {
        _tema.value = nuevoTema
    }

    // Funciones de Tarjetas
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

        _listaTarjetas.value = _listaTarjetas.value + nuevaTarjeta
    }

    fun eliminarTarjeta(index: Int) {
        val listaMutable = _listaTarjetas.value.toMutableList()
        if (index in listaMutable.indices) {
            listaMutable.removeAt(index)

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
                    imagenUrl = null
                )

                val idLeccionGenerado = repositorio.insertarLeccion(nuevaLeccion)

                val tarjetasFinales = _listaTarjetas.value.map { tarjeta ->
                    tarjeta.copy(idLeccion = idLeccionGenerado.toInt())
                }

                tarjetasFinales.forEach { repo ->
                    repositorio.insertarTarjeta(repo)
                }

                // Guardar TODOS los cuestionarios creados
                _listaCuestionarios.value.forEach { borrador ->
                    if (borrador.preguntas.isNotEmpty()) {
                        val nuevoCuestionario = EntidadCuestionario(
                            idLeccion = idLeccionGenerado.toInt(),
                            tituloQuiz = borrador.titulo
                        )
                        repositorio.insertarCuestionarioCompleto(
                            cuestionario = nuevoCuestionario,
                            preguntas = borrador.preguntas
                        )
                    }
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
        val idActivo = _cuestionarioActivoId.value ?: return

        val nuevaPregunta = EntidadPregunta(
            idCuestionario = 0, 
            enunciado = enunciado
        )
        val preguntaConRespuestas = PreguntaConRespuestas(nuevaPregunta, respuestas)

        // Actualizamos la lista de cuestionarios modificando SOLO el activo
        _listaCuestionarios.value = _listaCuestionarios.value.map { cuestionario ->
            if (cuestionario.idTemporal == idActivo) {
                cuestionario.copy(preguntas = cuestionario.preguntas + preguntaConRespuestas)
            } else {
                cuestionario
            }
        }
    }

    fun eliminarPregunta(index: Int) {
        val idActivo = _cuestionarioActivoId.value ?: return

        _listaCuestionarios.value = _listaCuestionarios.value.map { cuestionario ->
            if (cuestionario.idTemporal == idActivo) {
                val preguntasMutable = cuestionario.preguntas.toMutableList()
                if (index in preguntasMutable.indices) {
                    preguntasMutable.removeAt(index)
                }
                cuestionario.copy(preguntas = preguntasMutable)
            } else {
                cuestionario
            }
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