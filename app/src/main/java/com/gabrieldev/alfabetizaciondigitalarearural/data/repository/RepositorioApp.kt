package com.gabrieldev.alfabetizaciondigitalarearural.data.repository

import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.CuestionarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.IntentoLeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.LeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.TarjetaDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.UsuarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadCuestionario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta
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

    suspend fun defaultLeccionesIncorporacion() {
        val leccionesExistentes = obtenerLecciones()

        val yaExisteOnboarding = leccionesExistentes.any {
            it.uuidAutorOriginal == "onboarding_sistema"
        }

        if (yaExisteOnboarding) return

        // LECCIÓN 1: Propósito de la aplicación
        val leccionBienvenida = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Bienvenido a la App",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion1 = leccionDao.insertarLeccion(leccionBienvenida).toInt()

        val tarjetasBienvenida = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion1,
                ordenSecuencia = 1,
                contenidoTexto = "¡Bienvenido! Esta app te ayudará a aprender sobre tecnología de forma sencilla.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#4CAF50"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion1,
                ordenSecuencia = 2,
                contenidoTexto = "Podrás aprender con lecciones interactivas, crear tus propias lecciones y compartirlas con otros.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#2196F3"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion1,
                ordenSecuencia = 3,
                contenidoTexto = "Desliza hacia la izquierda para continuar aprendiendo sobre cómo usar la aplicación.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#FF9800"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetasBienvenida)

        val cuestionario1 = EntidadCuestionario(
            idLeccion = idLeccion1,
            tituloQuiz = "Evaluación: Bienvenida"
        )
        val idCuestionario1 = cuestionarioDao.insertarCuestionario(cuestionario1).toInt()

        val p1_1 = EntidadPregunta(
            idCuestionario = idCuestionario1,
            enunciado = "¿Cuál es el propósito principal de esta aplicación?"
        )
        val idP1_1 = cuestionarioDao.insertarPregunta(p1_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP1_1, textoOpcion = "Jugar videojuegos", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP1_1, textoOpcion = "Aprender sobre tecnología de forma sencilla", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP1_1, textoOpcion = "Ver videos", esCorrecta = false)
        ))

        val p1_2 = EntidadPregunta(
            idCuestionario = idCuestionario1,
            enunciado = "¿Qué puedes hacer con esta aplicación?"
        )
        val idP1_2 = cuestionarioDao.insertarPregunta(p1_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP1_2, textoOpcion = "Solo ver lecciones", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP1_2, textoOpcion = "Aprender, crear y compartir lecciones", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP1_2, textoOpcion = "Enviar mensajes", esCorrecta = false)
        ))

        // LECCIÓN 2: Uso de lecciones
        val leccionUso = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Cómo usar las Lecciones",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion2 = leccionDao.insertarLeccion(leccionUso).toInt()

        val tarjetasUso = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion2,
                ordenSecuencia = 1,
                contenidoTexto = "En la pestaña 'Lecciones' encontrarás todas las lecciones disponibles.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#9C27B0"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion2,
                ordenSecuencia = 2,
                contenidoTexto = "Toca una lección para ver sus tarjetas. Desliza para avanzar.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#00BCD4"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion2,
                ordenSecuencia = 3,
                contenidoTexto = "Al final de cada lección, podrás responder un cuestionario para practicar.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#4CAF50"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetasUso)

        val cuestionario2 = EntidadCuestionario(
            idLeccion = idLeccion2,
            tituloQuiz = "Evaluación: Uso de Lecciones"
        )
        val idCuestionario2 = cuestionarioDao.insertarCuestionario(cuestionario2).toInt()

        val p2_1 = EntidadPregunta(
            idCuestionario = idCuestionario2,
            enunciado = "¿Dónde encuentras todas las lecciones disponibles?"
        )
        val idP2_1 = cuestionarioDao.insertarPregunta(p2_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP2_1, textoOpcion = "En la pestaña 'Inicio'", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP2_1, textoOpcion = "En la pestaña 'Lecciones'", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP2_1, textoOpcion = "En la pestaña 'Perfil'", esCorrecta = false)
        ))

        val p2_2 = EntidadPregunta(
            idCuestionario = idCuestionario2,
            enunciado = "¿Cómo avanzas entre las tarjetas de una lección?"
        )
        val idP2_2 = cuestionarioDao.insertarPregunta(p2_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP2_2, textoOpcion = "Tocando un botón", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP2_2, textoOpcion = "Deslizando la pantalla", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP2_2, textoOpcion = "Sacudiendo el teléfono", esCorrecta = false)
        ))

        val p2_3 = EntidadPregunta(
            idCuestionario = idCuestionario2,
            enunciado = "¿Qué aparece al final de cada lección?"
        )
        val idP2_3 = cuestionarioDao.insertarPregunta(p2_3).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP2_3, textoOpcion = "Un video", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP2_3, textoOpcion = "Un cuestionario para practicar", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP2_3, textoOpcion = "Una imagen", esCorrecta = false)
        ))

        // LECCIÓN 3: Crear lecciones
        val leccionCrear = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Crea tus Lecciones",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion3 = leccionDao.insertarLeccion(leccionCrear).toInt()

        val tarjetasCrear = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion3,
                ordenSecuencia = 1,
                contenidoTexto = "Puedes crear tus propias lecciones tocando el botón '+' en la pestaña Lecciones.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#FF5722"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion3,
                ordenSecuencia = 2,
                contenidoTexto = "Agrega un título, tema y crea tarjetas con el contenido que quieras enseñar.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#3F51B5"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion3,
                ordenSecuencia = 3,
                contenidoTexto = "También puedes agregar cuestionarios para que otros practiquen lo aprendido.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#009688"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetasCrear)

        val cuestionario3 = EntidadCuestionario(
            idLeccion = idLeccion3,
            tituloQuiz = "Evaluación: Crear Lecciones"
        )
        val idCuestionario3 = cuestionarioDao.insertarCuestionario(cuestionario3).toInt()

        val p3_1 = EntidadPregunta(
            idCuestionario = idCuestionario3,
            enunciado = "¿Cómo creas una nueva lección?"
        )
        val idP3_1 = cuestionarioDao.insertarPregunta(p3_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP3_1, textoOpcion = "Tocando el botón '+' en Lecciones", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP3_1, textoOpcion = "En la pestaña Perfil", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP3_1, textoOpcion = "Descargándola de internet", esCorrecta = false)
        ))

        val p3_2 = EntidadPregunta(
            idCuestionario = idCuestionario3,
            enunciado = "¿Qué elementos puedes agregar a una lección?"
        )
        val idP3_2 = cuestionarioDao.insertarPregunta(p3_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP3_2, textoOpcion = "Solo texto", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP3_2, textoOpcion = "Título, tema, tarjetas y cuestionarios", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP3_2, textoOpcion = "Solo imágenes", esCorrecta = false)
        ))

        // LECCIÓN 4: Compartir
        val leccionCompartir = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Comparte Conocimiento",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion4 = leccionDao.insertarLeccion(leccionCompartir).toInt()

        val tarjetasCompartir = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 1,
                contenidoTexto = "Puedes compartir lecciones con otros dispositivos cercanos sin necesidad de internet.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#E91E63"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 2,
                contenidoTexto = "En cada lección, toca el botón 'Compartir' para enviarla a dispositivos cercanos.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#673AB7"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 3,
                contenidoTexto = "Importante: En dispositivos Android 9 o anteriores, debes activar la ubicación (GPS) para poder compartir.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#FF9800"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 4,
                contenidoTexto = "¡Listo! Ahora ya sabes cómo usar la aplicación. ¡Comienza a aprender!",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#4CAF50"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetasCompartir)

        val cuestionario4 = EntidadCuestionario(
            idLeccion = idLeccion4,
            tituloQuiz = "Evaluación: Compartir Lecciones"
        )
        val idCuestionario4 = cuestionarioDao.insertarCuestionario(cuestionario4).toInt()

        val p4_1 = EntidadPregunta(
            idCuestionario = idCuestionario4,
            enunciado = "¿Necesitas internet para compartir lecciones?"
        )
        val idP4_1 = cuestionarioDao.insertarPregunta(p4_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP4_1, textoOpcion = "Sí, siempre", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP4_1, textoOpcion = "No, se comparten entre dispositivos cercanos", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP4_1, textoOpcion = "Solo con WiFi", esCorrecta = false)
        ))

        val p4_2 = EntidadPregunta(
            idCuestionario = idCuestionario4,
            enunciado = "¿Dónde encuentras el botón para compartir una lección?"
        )
        val idP4_2 = cuestionarioDao.insertarPregunta(p4_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP4_2, textoOpcion = "En el perfil", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP4_2, textoOpcion = "En cada lección", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP4_2, textoOpcion = "En la configuración", esCorrecta = false)
        ))
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

            val respuestasParaInsertar = p.respuestas.map { it.copy(idPregunta = idPregunta) }
            cuestionarioDao.insertarRespuestas(respuestasParaInsertar)
        }
    }

    suspend fun actualizarLeccionCompleta(
        idLeccion: Int,
        titulo: String,
        tema: String,
        tarjetas: List<EntidadTarjeta>,
        cuestionariosTemporales: List<Pair<String, List<PreguntaConRespuestas>>>
    ) {
        val leccionOriginal: EntidadLeccion = leccionDao.obtenerLeccionPorId(idLeccion) ?: return

        val leccionActualizada: EntidadLeccion = leccionOriginal.copy(
            titulo = titulo,
            tema = tema
        )

        //actualizamos las tarjetas con una la nueva leccion obtenida
        leccionDao.actualizarLeccion(leccionActualizada)

        //eliminamos las tarjetas, ya tenemos las nuevas como parametro de esta nueva funcion,
        tarjetaDao.eliminarTarjetasDeLeccion(idLeccion)

        val tarjetasParaInsertar = tarjetas.map { it.copy(idLeccion = idLeccion) }
        tarjetaDao.insertarTarjetas(tarjetasParaInsertar)

        //eliminanos las lecciones (en cascada) y agregamos las nuevas
        cuestionarioDao.eliminarCuestionariosDeLeccion(idLeccion)

        //recorremos cada par de titulo y lisat de preguntas
        cuestionariosTemporales.forEach { (tituloDelCuestionario, listaDePreguntas) ->

            val nuevoCuestionario = EntidadCuestionario(
                idLeccion = idLeccion,
                tituloQuiz = tituloDelCuestionario
            )

            insertarCuestionarioCompleto(
                cuestionario = nuevoCuestionario,
                preguntas = listaDePreguntas
            )
        }
    }

    suspend fun obtenerLeccionConCuestionarios(idLeccion: Int): Pair<List<EntidadTarjeta>, List<CuestionarioConPreguntas>> {
        val tarjetas = tarjetaDao.obtenerTarjetasPorLeccion(idLeccion)

        val cuestionarios = cuestionarioDao.obtenerCuestionariosPorLeccion(idLeccion)

        //llenamos listaCompletaCuestionarios despues de la operacion
        val listaCompletaCuestionarios = cuestionarios.map { cuestionario ->
            val preguntas = cuestionarioDao.obtenerPreguntasPorCuestionario(cuestionario.idCuestionario)

            val preguntasConRespuestas = preguntas.map { pregunta ->
                val respuestas = cuestionarioDao.obtenerRespuestasPorPregunta(pregunta.idPregunta)
                PreguntaConRespuestas(pregunta, respuestas)
            }
            //res final
            CuestionarioConPreguntas(cuestionario, preguntasConRespuestas)
        }
        return Pair(tarjetas, listaCompletaCuestionarios)
    }

    suspend fun obtenerLeccionPorId(id: Int): EntidadLeccion? {
        return leccionDao.obtenerLeccionPorId(id)
    }

    suspend fun eliminarLeccionPorId(id: Int) {
        leccionDao.eliminarLeccionPorId(id)
    }

    suspend fun obtenerPromedioDeLeccion(idUsuario: Int, idLeccion: Int): Double {
        return intentoLeccionDao.obtenerPromedioPorLeccionEspecifica(idUsuario, idLeccion) ?: 0.0
    }

    suspend fun obtenerLeccionesRealizadasPorUsuario(idUsuario: Int): List<EntidadLeccion> {
        return intentoLeccionDao.obtenerLeccionesRealizadasPorUsuario(idUsuario)
    }
}
data class CuestionarioConPreguntas(
    val cuestionario: EntidadCuestionario,
    val preguntas: List<PreguntaConRespuestas>
)

data class PreguntaConRespuestas(
    val pregunta: EntidadPregunta,
    val respuestas: List<EntidadRespuesta>
)