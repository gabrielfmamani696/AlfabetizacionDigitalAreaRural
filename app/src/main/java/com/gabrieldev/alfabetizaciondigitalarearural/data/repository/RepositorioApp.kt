package com.gabrieldev.alfabetizaciondigitalarearural.data.repository

import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.CuestionarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.IntentoLeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.LeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.TarjetaDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.UsuarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadCuestionario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import kotlinx.coroutines.flow.Flow
import java.util.UUID

//intermediario de datos
class RepositorioApp(
    private val usuarioDao: UsuarioDao,
    private val leccionDao: LeccionDao,
    private val tarjetaDao: TarjetaDao,
    private val cuestionarioDao: CuestionarioDao,
    private val intentoLeccionDao: IntentoLeccionDao,
    ) {

    // Obtener el usuario activo (para la pantalla principal)
    val ultimoUsuario: Flow<EntidadUsuario?> = usuarioDao.obtenerUltimoUsuario()

    // Crear un nuevo usuario
    suspend fun crearUsuario(nombre: String, avatarId: Int = 0) {
        val nuevoUsuario = EntidadUsuario(
            alias = nombre,
            ultimaActividad = System.currentTimeMillis(),
            uuidUsuario = UUID.randomUUID().toString()
        )
        usuarioDao.insertarUsuario(nuevoUsuario)
    }

    suspend fun existeAlgunUsuario(): Boolean {
        return usuarioDao.contarUsuarios() > 0
    }

    suspend fun obtenerLecciones(): List<EntidadLeccion> {
        return leccionDao.consultarLecciones()
    }

    suspend fun defaultLecciones() {
        val lecciones = obtenerLecciones()
        if(lecciones.isEmpty()){
            val leccion1 = EntidadLeccion(
                uuidGlobal = UUID.randomUUID().toString(),
                titulo = "Encender la Computadora",
                tema = "Hardware",
                autorOriginal = "Sistema",
                fechaCreacion = System.currentTimeMillis(),
                creadaPorUsuario = false,
                uuidAutorOriginal = "sistema",
                imagenUrl = "url_o_path_placeholder"
            )
            val leccion2 = EntidadLeccion(
                uuidGlobal = UUID.randomUUID().toString(),
                titulo = "El Ratón y el Teclado",
                tema = "Hardware",
                autorOriginal = "Sistema",
                fechaCreacion = System.currentTimeMillis(),
                creadaPorUsuario = false,
                uuidAutorOriginal = "sistema",
                imagenUrl = "url_o_path_placeholder"
            )
            leccionDao.insertarLeccion(leccion1)
            leccionDao.insertarLeccion(leccion2)
        }

    }

    suspend fun defaultTarjetas() {
        val tarjetasExistentes = tarjetaDao.obtenerTarjetasPorLeccion(1)
        if (tarjetasExistentes.isEmpty()) {
            val tarjetas = listOf(
                EntidadTarjeta(
                    idLeccion = 1,
                    ordenSecuencia = 1,
                    contenidoTexto = "Bienvenido a la lección sobre cómo encender la computadora",
                    tipoFondo = "IMAGEN",
                    dataFondo = "tarjeta_encender_computadora"
                ),
                EntidadTarjeta(
                    idLeccion = 1,
                    ordenSecuencia = 2,
                    contenidoTexto = "Primero, busca el botón de encendido en tu computadora",
                    tipoFondo = "COLOR_SOLIDO",
                    dataFondo = "#2196F3"
                ),
                EntidadTarjeta(
                    idLeccion = 1,
                    ordenSecuencia = 3,
                    contenidoTexto = "El botón suele tener el símbolo de encendido ⏻",
                    tipoFondo = "COLOR_SOLIDO",
                    dataFondo = "#FF9800"
                ),
                EntidadTarjeta(
                    idLeccion = 1,
                    ordenSecuencia = 4,
                    contenidoTexto = "Presiona el botón de encendido una vez",
                    tipoFondo = "COLOR_SOLIDO",
                    dataFondo = "#9C27B0"
                ),
                EntidadTarjeta(
                    idLeccion = 1,
                    ordenSecuencia = 5,
                    contenidoTexto = "Espera mientras la computadora se inicia. Esto puede tomar unos minutos",
                    tipoFondo = "COLOR_SOLIDO",
                    dataFondo = "#00BCD4"
                ),
                EntidadTarjeta(
                    idLeccion = 1,
                    ordenSecuencia = 6,
                    contenidoTexto = "¡Felicidades! Has encendido la computadora correctamente",
                    tipoFondo = "COLOR_SOLIDO",
                    dataFondo = "#4CAF50"
                )
            )
            tarjetaDao.insertarTarjetas(tarjetas)
        }
    }

    suspend fun obtenerTarjetasPorLeccion(idLeccion: Int): List<EntidadTarjeta> {
        return tarjetaDao.obtenerTarjetasPorLeccion(idLeccion)
    }

    suspend fun obtenerCuestionarioAleatorio(idLeccion: Int): CuestionarioConPreguntas? {
        val cuestionarios = cuestionarioDao.obtenerCuestionariosPorLeccion(idLeccion)
        if (cuestionarios.isEmpty()) return null

        val cuestionario = cuestionarios.random()
        val preguntas = cuestionarioDao.obtenerPreguntasPorCuestionario(cuestionario.idCuestionario)
        
        val preguntasConRespuestas = preguntas.map { pregunta ->
            val respuestas = cuestionarioDao.obtenerRespuestasPorPregunta(pregunta.idPregunta)
            PreguntaConRespuestas(pregunta, respuestas)
        }

        return CuestionarioConPreguntas(cuestionario, preguntasConRespuestas)
    }

    suspend fun insertarIntento(intento: EntidadIntentoLeccion) {
        intentoLeccionDao.insertarIntento(intento)
    }

    suspend fun defaultCuestionarios() {
        val cuestionariosExistentes = cuestionarioDao.obtenerCuestionariosPorLeccion(1)
        if (cuestionariosExistentes.isEmpty()) {
            val cuestionario = EntidadCuestionario(
                idLeccion = 1,
                tituloQuiz = "Evaluación: Encender la Computadora"
            )
            val idCuestionario = cuestionarioDao.insertarCuestionario(cuestionario).toInt()

            val p1 = com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta(
                idCuestionario = idCuestionario,
                enunciado = "¿Cuál es el primer paso para encender la computadora?"
            )
            val idP1 = cuestionarioDao.insertarPregunta(p1).toInt()
            cuestionarioDao.insertarRespuestas(listOf(
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP1, textoOpcion = "Presionar el botón de la pantalla", esCorrecta = false),
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP1, textoOpcion = "Buscar y presionar el botón de encendido", esCorrecta = true),
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP1, textoOpcion = "Mover el ratón", esCorrecta = false)
            ))

            val p2 = com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta(
                idCuestionario = idCuestionario,
                enunciado = "¿Qué símbolo suele tener el botón de encendido?"
            )
            val idP2 = cuestionarioDao.insertarPregunta(p2).toInt()
            cuestionarioDao.insertarRespuestas(listOf(
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP2, textoOpcion = "Un triángulo", esCorrecta = false),
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP2, textoOpcion = "Un círculo atravesado por una línea (⏻)", esCorrecta = true),
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP2, textoOpcion = "Una estrella", esCorrecta = false)
            ))

            val p3 = com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta(
                idCuestionario = idCuestionario,
                enunciado = "Después de presionar el botón, ¿qué debes hacer?"
            )
            val idP3 = cuestionarioDao.insertarPregunta(p3).toInt()
            cuestionarioDao.insertarRespuestas(listOf(
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP3, textoOpcion = "Presionarlo muchas veces rápido", esCorrecta = false),
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP3, textoOpcion = "Desconectar el cable", esCorrecta = false),
                com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta(idPregunta = idP3, textoOpcion = "Esperar a que el sistema inicie", esCorrecta = true)
            ))
        }
    }

    suspend fun insertarLeccion(leccion: EntidadLeccion): Long {
        return leccionDao.insertarLeccion(leccion)
    }

    suspend fun insertarTarjeta(tarjeta: EntidadTarjeta): Long {
        return tarjetaDao.insertarTarjeta(tarjeta);
    }

    suspend fun insertarCuestionarioCompleto(
        cuestionario: EntidadCuestionario,
        preguntas: List<PreguntaConRespuestas>
    ) {
        val idCuestionario = cuestionarioDao.insertarCuestionario(cuestionario).toInt()

        preguntas.forEach { p ->
            // Vinculamos la pregunta al cuestionario creado
            val preguntaParaInsertar = p.pregunta.copy(idCuestionario = idCuestionario)
            val idPregunta = cuestionarioDao.insertarPregunta(preguntaParaInsertar).toInt()
            
            // 3. Vinculamos las respuestas a esa pregunta
            val respuestasParaInsertar = p.respuestas.map { it.copy(idPregunta = idPregunta) }
            cuestionarioDao.insertarRespuestas(respuestasParaInsertar)
        }
    }
}
data class CuestionarioConPreguntas(
    val cuestionario: EntidadCuestionario,
    val preguntas: List<PreguntaConRespuestas>
)

data class PreguntaConRespuestas(
    val pregunta: com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta,
    val respuestas: List<com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta>
)