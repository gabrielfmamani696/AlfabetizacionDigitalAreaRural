package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.viewmodels

import android.net.Uri
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
import com.gabrieldev.alfabetizaciondigitalarearural.utils.FileStorageHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

//mantiene el estado de los datos pese a cambios en la pantalla
class CrearLeccionViewModel(
    private val repositorio: RepositorioApp
): ViewModel() {

    private var _idLeccionEdicion: Int? = null

    //estado de flujo mutable en el tiepo, acceso de escritura y lectura de variable _titulo
    private val _titulo = MutableStateFlow("")
    //estado de flujo, solo acceso de lectura
    val titulo = _titulo.asStateFlow()

    private val _tema = MutableStateFlow("")
    val tema = _tema.asStateFlow()

    private val _theme = MutableStateFlow("") // Alias temporal
    
    // multiples cuestionarios
    data class CuestionarioBorrador(
        val idTemporal: String = UUID.randomUUID().toString(),
        val titulo: String,
        val preguntas: List<PreguntaConRespuestas> = emptyList()
    )

    private val _listaCuestionarios = MutableStateFlow<List<CuestionarioBorrador>>(emptyList())
    val listaCuestionarios = _listaCuestionarios.asStateFlow()

    // cuestionario seleccionado para editar
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

    private val _imagenPortada = MutableStateFlow<String?>(null) // URI temporal como string
    val imagenPortada = _imagenPortada.asStateFlow()

    fun actualizarImagenPortada(uri: String?) {
        _imagenPortada.value = uri
    }

    // Recibimos Context para poder guardar los archivos físicos
    fun guardarLeccion(context: android.content.Context) {

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
                var pathPortadaFinal: String? = null
                _imagenPortada.value?.let { uriStr ->
                    if (uriStr.startsWith("content://")) {
                        val uri = Uri.parse(uriStr)
                        pathPortadaFinal = FileStorageHelper.guardarImagenDesdeUri(context, uri)
                    } else {
                        pathPortadaFinal = uriStr
                    }
                }

                val tarjetasProcesadas = _listaTarjetas.value.map { tarjeta ->
                    if (tarjeta.tipoFondo == "IMAGEN" && tarjeta.dataFondo.startsWith("content://")) {
                        val uri = Uri.parse(tarjeta.dataFondo)
                        val pathFinal = FileStorageHelper.guardarImagenDesdeUri(context, uri)
                        tarjeta.copy(dataFondo = pathFinal ?: tarjeta.dataFondo)
                    } else {
                        tarjeta
                    }
                }

                val cuestionariosParaRepo = _listaCuestionarios.value.map { borrador ->
                    Pair(borrador.titulo, borrador.preguntas)
                }

                if (_idLeccionEdicion != null && _idLeccionEdicion != 0) { //edicion
                    repositorio.actualizarLeccionCompleta(
                        idLeccion = _idLeccionEdicion!!,
                        titulo = _titulo.value,
                        tema = _tema.value,
                        tarjetas = tarjetasProcesadas, // Usamos las procesadas
                        cuestionariosTemporales = cuestionariosParaRepo
                    )
                    val leccionExistente = repositorio.obtenerLeccionPorId(_idLeccionEdicion!!)
                    if (leccionExistente != null) {
                         repositorio.insertarLeccion(leccionExistente.copy(
                             titulo = _titulo.value,
                             tema = _tema.value,
                             imagenUrl = pathPortadaFinal ?: leccionExistente.imagenUrl
                         ))
                    }
                    
                    _mensajeUsuario.value = "Lección actualizada con éxito"
                } else {

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
                        imagenUrl = pathPortadaFinal // Guardamos la ruta final
                    )

                    val idLeccionGenerado = repositorio.insertarLeccion(nuevaLeccion)

                    val tarjetasFinales = tarjetasProcesadas.map { tarjeta -> // Usamos las procesadas
                        tarjeta.copy(idLeccion = idLeccionGenerado.toInt())
                    }

                    tarjetasFinales.forEach { repo ->
                        repositorio.insertarTarjeta(repo)
                    }

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

                }
                _navegarAtras.value = true

            } catch (e: Exception) {
                _mensajeUsuario.value = "Error al guardar: ${e.localizedMessage}"
                e.printStackTrace()
            }
        }
    }

    fun actualizarTituloCuestionario(nuevoTitulo: String) {
        val idActivo = _cuestionarioActivoId.value ?: return

        _listaCuestionarios.value = _listaCuestionarios.value.map { cuestionario ->
            if (cuestionario.idTemporal == idActivo) {
                cuestionario.copy(titulo = nuevoTitulo)
            } else {
                cuestionario
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

    fun cargarDatosParaEdicion(idLeccion: Int) {
        _idLeccionEdicion = idLeccion

        viewModelScope.launch{
            val leccion = repositorio.obtenerLeccionPorId(idLeccion)
            if (leccion != null) {
                _titulo.value = leccion.titulo
                _tema.value = leccion.tema
            }

            val (tarjetasBD, cuestionariosBD) = repositorio.obtenerLeccionConCuestionarios(idLeccion)

            if (leccion != null) {
                _titulo.value = leccion.titulo
                _tema.value = leccion.tema
                _imagenPortada.value = leccion.imagenUrl
            }

            _listaTarjetas.value = tarjetasBD
            //a cada uno de los cuestionarios, le asigamos nuevos datos y se asigna a borradores
            val borradores = cuestionariosBD.map { item ->
                CuestionarioBorrador(
                    idTemporal = UUID.randomUUID().toString(),
                    titulo = item.cuestionario.tituloQuiz,
                    preguntas = item.preguntas
                )
            }
            _listaCuestionarios.value = borradores
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