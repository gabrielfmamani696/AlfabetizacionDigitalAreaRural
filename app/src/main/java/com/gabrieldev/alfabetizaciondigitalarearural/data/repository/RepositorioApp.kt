package com.gabrieldev.alfabetizaciondigitalarearural.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.CuestionarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.IntentoLeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.LeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.LogroNotificadoDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.TarjetaDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.UsuarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadCuestionario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLogroNotificado
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.modelos.EstadoLogros
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.modelos.TipoLogro
import com.gabrieldev.alfabetizaciondigitalarearural.data.workers.NotificacionWorker
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

//intermediario de datos
class RepositorioApp(
    private val usuarioDao: UsuarioDao,
    private val leccionDao: LeccionDao,
    private val tarjetaDao: TarjetaDao,
    private val cuestionarioDao: CuestionarioDao,
    private val intentoLeccionDao: IntentoLeccionDao,
    private val logroNotificadoDao: LogroNotificadoDao,
    ) {

    // Obtener el usuario activo (para la pantalla principal)
    val usuarioActivo: Flow<EntidadUsuario?> = usuarioDao.obtenerUsuarioActivo()

    // Crear un nuevo usuario
    suspend fun crearUsuario(nombre: String, avatarId: Int = 0) {

        // Verificar si es el primer usuario
        val usuariosExistentes = usuarioDao.obtenerTodosLosUsuarios()
        val esPrimerUsuario = usuariosExistentes.isEmpty()

        val nuevoUsuario = EntidadUsuario(
            alias = nombre,
            ultimaActividad = System.currentTimeMillis(),
            uuidUsuario = UUID.randomUUID().toString(),
            activo = esPrimerUsuario
        )
        usuarioDao.insertarUsuario(nuevoUsuario)
    }

    suspend fun cambiarUsuarioActivo(idUsuario: Int) {
        usuarioDao.desactivarTodosLosUsuarios()
        usuarioDao.activarUsuario(idUsuario)
    }

    suspend fun obtenerTodosLosUsuarios(): List<EntidadUsuario> {
        return usuarioDao.obtenerTodosLosUsuarios()
    }

    suspend fun actualizarConfiguracionNotificaciones(
        idUsuario: Int,
        habilitadas: Boolean,
        hora: Int,
        minuto: Int
    ) {
        usuarioDao.actualizarConfiguracionNotificaciones(idUsuario, habilitadas, hora, minuto)
    }

    suspend fun actualizarAliasUsuario(idUsuario: Int, nuevoAlias: String) {
        usuarioDao.actualizarAlias(idUsuario, nuevoAlias)
    }

    suspend fun eliminarUsuario(idUsuario: Int): Boolean {
        val todosLosUsuarios = usuarioDao.obtenerTodosLosUsuarios()
        
        // No permitir eliminar si es el único usuario
        if (todosLosUsuarios.size <= 1) {
            return false
        }

        val usuarioAEliminar = todosLosUsuarios.find { it.idUsuario == idUsuario }
        
        // Si el usuario a eliminar es el activo, activar otro
        if (usuarioAEliminar?.activo == true) {
            val otroUsuario = todosLosUsuarios.find { it.idUsuario != idUsuario }
            otroUsuario?.let {
                usuarioDao.activarUsuario(it.idUsuario)
            }
        }

        usuarioDao.eliminarUsuario(idUsuario)
        return true
    }

    suspend fun actualizarRachaUsuario(idUsuario: Int) {
        val usuario = usuarioDao.obtenerUsuarioPorId(idUsuario) ?: return
        val intentos = intentoLeccionDao.obtenerIntentosPorUsuario(idUsuario)
        
        if (intentos.isEmpty()) {
            usuarioDao.actualizarRacha(idUsuario, 0)
            return
        }

        val fechasUnicas = intentos
            .map { intento ->
                val calendar = java.util.Calendar.getInstance()
                calendar.timeInMillis = intento.fechaIntento
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            .distinct()
            .sortedDescending()

        val hoy = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        val ayer = hoy - (24 * 60 * 60 * 1000)

        var racha = 0
        
        if (fechasUnicas.contains(hoy) || fechasUnicas.contains(ayer)) {
            racha = 1
            var fechaEsperada = if (fechasUnicas.contains(hoy)) ayer else hoy - (24 * 60 * 60 * 1000)
            
            for (fecha in fechasUnicas) {
                if (fecha == fechaEsperada) {
                    racha++
                    fechaEsperada -= (24 * 60 * 60 * 1000)
                } else if (fecha < fechaEsperada) {
                    break
                }
            }
        }

        usuarioDao.actualizarRacha(idUsuario, racha)
    }

    suspend fun marcarLogroComoNotificado(idUsuario: Int, logro: TipoLogro) {
        val entidadLN = EntidadLogroNotificado(
            idUsuario = idUsuario,
            tipoLogro = logro.name
        )
        logroNotificadoDao.marcarComoNotificado(entidadLN)
    }

    suspend fun obtenerLogrosNuevos(idUsuario: Int): List<TipoLogro> {
        val estadoActual = obtenerEstadosLogros(idUsuario)

        return estadoActual.logrosDesbloqueados.filter { logro ->
            !logroNotificadoDao.fueNotificado(idUsuario, logro.name)
        }
    }

    fun usuarioInactivoPorDias(usuario: EntidadUsuario, dias: Int): Boolean {
        val milisegundosPorDia = 24 * 60 * 60 * 1000L
        val tiempoInactivo = System.currentTimeMillis() - usuario.ultimaActividad
        return tiempoInactivo > (dias * milisegundosPorDia)
    }
    /**
     * Actualiza la última actividad del usuario al momento actual.
     * Llamar cada vez que el usuario interactúe con la app.
     */
    suspend fun actualizarUltimaActividad(idUsuario: Int) {
        // Necesitarás agregar esta query en UsuarioDao:
        // @Query("UPDATE usuarios SET ultima_actividad = :timestamp WHERE id_usuario = :idUsuario")
        // suspend fun actualizarUltimaActividad(idUsuario: Int, timestamp: Long)

        usuarioDao.actualizarUltimaActividad(idUsuario, System.currentTimeMillis())
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

        // LECCIÓN 1: Introducción a la Aplicación
        val leccion1 = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Bienvenido a la Aplicación",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion1 = leccionDao.insertarLeccion(leccion1).toInt()

        val tarjetas1 = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion1,
                ordenSecuencia = 1,
                contenidoTexto = "Esta aplicación está diseñada para ayudarte a aprender de manera sencilla y efectiva, dividiendo el conocimiento en pequeñas lecciones que son fáciles de entender y recordar.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#4CAF50"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion1,
                ordenSecuencia = 2,
                contenidoTexto = "Aprende a tu ritmo: El contenido está organizado en lecciones cortas que no sobrecargan tu mente.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#2196F3"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion1,
                ordenSecuencia = 3,
                contenidoTexto = "Funciona sin Internet: Puedes usar la aplicación completamente sin conexión, ideal para zonas rurales o con Internet limitado.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#FF9800"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion1,
                ordenSecuencia = 4,
                contenidoTexto = "Comparte conocimiento: Crea tus propias lecciones y compártelas con otros, incluso sin Internet.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#9C27B0"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetas1)

        val cuestionario1 = EntidadCuestionario(
            idLeccion = idLeccion1,
            tituloQuiz = "Evaluación: Introducción"
        )
        val idCuestionario1 = cuestionarioDao.insertarCuestionario(cuestionario1).toInt()

        val p1_1 = EntidadPregunta(
            idCuestionario = idCuestionario1,
            enunciado = "¿Cómo está organizado el contenido en esta aplicación?"
        )
        val idP1_1 = cuestionarioDao.insertarPregunta(p1_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP1_1, textoOpcion = "En lecciones largas y complejas", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP1_1, textoOpcion = "En lecciones cortas y fáciles de entender", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP1_1, textoOpcion = "En videos largos", esCorrecta = false)
        ))

        val p1_2 = EntidadPregunta(
            idCuestionario = idCuestionario1,
            enunciado = "¿Necesitas Internet para usar la aplicación?"
        )
        val idP1_2 = cuestionarioDao.insertarPregunta(p1_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP1_2, textoOpcion = "Sí, siempre", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP1_2, textoOpcion = "No, funciona completamente sin conexión", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP1_2, textoOpcion = "Solo para compartir", esCorrecta = false)
        ))

        // LECCIÓN 2: Sección Inicio
        val leccion2 = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Tu Panel de Progreso",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion2 = leccionDao.insertarLeccion(leccion2).toInt()

        val tarjetas2 = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion2,
                ordenSecuencia = 1,
                contenidoTexto = "La sección Inicio es tu punto de partida. Aquí verás tu racha de aprendizaje ⚡, que muestra cuántos días consecutivos has completado lecciones.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#00BCD4"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion2,
                ordenSecuencia = 2,
                contenidoTexto = "Próximo Logro 🏆: Te muestra qué insignia puedes desbloquear próximamente con una barra de progreso. Hay 4 logros: Primer Paso, Aprendiz, Perfeccionista y Estudioso.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#FF9800"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion2,
                ordenSecuencia = 3,
                contenidoTexto = "Tip del Día 💡: Cada día verás un consejo diferente que te enseña cómo obtener las insignias y te recuerda funciones útiles de la aplicación.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#4CAF50"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetas2)

        val cuestionario2 = EntidadCuestionario(
            idLeccion = idLeccion2,
            tituloQuiz = "Evaluación: Panel de Inicio"
        )
        val idCuestionario2 = cuestionarioDao.insertarCuestionario(cuestionario2).toInt()

        val p2_1 = EntidadPregunta(
            idCuestionario = idCuestionario2,
            enunciado = "¿Qué muestra la racha de aprendizaje?"
        )
        val idP2_1 = cuestionarioDao.insertarPregunta(p2_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP2_1, textoOpcion = "Cuántas lecciones has completado en total", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP2_1, textoOpcion = "Cuántos días consecutivos has completado lecciones", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP2_1, textoOpcion = "Tu calificación promedio", esCorrecta = false)
        ))

        val p2_2 = EntidadPregunta(
            idCuestionario = idCuestionario2,
            enunciado = "¿Cuántos logros hay disponibles en la aplicación?"
        )
        val idP2_2 = cuestionarioDao.insertarPregunta(p2_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP2_2, textoOpcion = "2 logros", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP2_2, textoOpcion = "4 logros", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP2_2, textoOpcion = "10 logros", esCorrecta = false)
        ))

        // LECCIÓN 3: Sección Lecciones
        val leccion3 = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Aprende y Comparte",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion3 = leccionDao.insertarLeccion(leccion3).toInt()

        val tarjetas3 = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion3,
                ordenSecuencia = 1,
                contenidoTexto = "En la sección Lecciones puedes ver todas las lecciones disponibles. Toca una para estudiarla, lee las tarjetas y responde el cuestionario al final.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#9C27B0"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion3,
                ordenSecuencia = 2,
                contenidoTexto = "Crear lecciones ✏️: Toca el botón '+' para crear tus propias lecciones. Agrega título, tema, tarjetas con texto e imágenes, y preguntas de opción múltiple.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#3F51B5"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion3,
                ordenSecuencia = 3,
                contenidoTexto = "Compartir sin Internet 📤: Toca el botón de compartir en cualquier lección para enviarla a dispositivos cercanos sin necesidad de Internet usando Nearby.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#E91E63"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion3,
                ordenSecuencia = 4,
                contenidoTexto = "Sistema de calificación: Necesitas 60% o más para aprobar. Puedes repetir las lecciones cuantas veces quieras y tu promedio se actualiza automáticamente.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#009688"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetas3)

        val cuestionario3 = EntidadCuestionario(
            idLeccion = idLeccion3,
            tituloQuiz = "Evaluación: Sección Lecciones"
        )
        val idCuestionario3 = cuestionarioDao.insertarCuestionario(cuestionario3).toInt()

        val p3_1 = EntidadPregunta(
            idCuestionario = idCuestionario3,
            enunciado = "¿Cómo creas una nueva lección?"
        )
        val idP3_1 = cuestionarioDao.insertarPregunta(p3_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP3_1, textoOpcion = "Tocando el botón '+' en Lecciones", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP3_1, textoOpcion = "En la sección Perfil", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP3_1, textoOpcion = "Descargándola de Internet", esCorrecta = false)
        ))

        val p3_2 = EntidadPregunta(
            idCuestionario = idCuestionario3,
            enunciado = "¿Qué porcentaje necesitas para aprobar una lección?"
        )
        val idP3_2 = cuestionarioDao.insertarPregunta(p3_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP3_2, textoOpcion = "50%", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP3_2, textoOpcion = "60%", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP3_2, textoOpcion = "100%", esCorrecta = false)
        ))

        val p3_3 = EntidadPregunta(
            idCuestionario = idCuestionario3,
            enunciado = "¿Necesitas Internet para compartir lecciones?"
        )
        val idP3_3 = cuestionarioDao.insertarPregunta(p3_3).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP3_3, textoOpcion = "Sí, siempre", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP3_3, textoOpcion = "No, se comparten entre dispositivos cercanos", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP3_3, textoOpcion = "Solo con WiFi", esCorrecta = false)
        ))

        // LECCIÓN 4: Sección Perfil
        val leccion4 = EntidadLeccion(
            uuidGlobal = UUID.randomUUID().toString(),
            titulo = "Gestiona tu Cuenta",
            tema = "Tutorial",
            autorOriginal = "Sistema",
            fechaCreacion = System.currentTimeMillis(),
            creadaPorUsuario = false,
            uuidAutorOriginal = "onboarding_sistema",
            imagenUrl = null
        )

        val idLeccion4 = leccionDao.insertarLeccion(leccion4).toInt()

        val tarjetas4 = listOf(
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 1,
                contenidoTexto = "En Perfil puedes ver tu información, promedio general y lecciones completadas. También puedes gestionar múltiples usuarios en el mismo dispositivo.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#FF5722"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 2,
                contenidoTexto = "Gestión de usuarios 👥: Crea nuevos perfiles, cambia entre usuarios, edita nombres (✏️) o elimina perfiles (🗑️). Ideal para familias o grupos de estudio.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#673AB7"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 3,
                contenidoTexto = "Tus logros 🏆: Visualiza tus insignias desbloqueadas (en color) y bloqueadas (en gris). Toca una insignia para ver cómo desbloquearla.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#FF9800"
            ),
            EntidadTarjeta(
                idLeccion = idLeccion4,
                ordenSecuencia = 4,
                contenidoTexto = "Notificaciones 🔔: Programa recordatorios diarios para estudiar. Selecciona la hora que prefieres y la app te recordará cada día. Puedes desactivarlas cuando quieras.",
                tipoFondo = "COLOR_SOLIDO",
                dataFondo = "#4CAF50"
            )
        )

        tarjetaDao.insertarTarjetas(tarjetas4)

        val cuestionario4 = EntidadCuestionario(
            idLeccion = idLeccion4,
            tituloQuiz = "Evaluación: Sección Perfil"
        )
        val idCuestionario4 = cuestionarioDao.insertarCuestionario(cuestionario4).toInt()

        val p4_1 = EntidadPregunta(
            idCuestionario = idCuestionario4,
            enunciado = "¿Puedes tener múltiples usuarios en el mismo dispositivo?"
        )
        val idP4_1 = cuestionarioDao.insertarPregunta(p4_1).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP4_1, textoOpcion = "No, solo uno", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP4_1, textoOpcion = "Sí, puedes crear varios perfiles", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP4_1, textoOpcion = "Solo con Internet", esCorrecta = false)
        ))

        val p4_2 = EntidadPregunta(
            idCuestionario = idCuestionario4,
            enunciado = "¿Cómo se ven las insignias que aún no has desbloqueado?"
        )
        val idP4_2 = cuestionarioDao.insertarPregunta(p4_2).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP4_2, textoOpcion = "En color", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP4_2, textoOpcion = "En gris", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP4_2, textoOpcion = "No se ven", esCorrecta = false)
        ))

        val p4_3 = EntidadPregunta(
            idCuestionario = idCuestionario4,
            enunciado = "¿Para qué sirven las notificaciones?"
        )
        val idP4_3 = cuestionarioDao.insertarPregunta(p4_3).toInt()
        cuestionarioDao.insertarRespuestas(listOf(
            EntidadRespuesta(idPregunta = idP4_3, textoOpcion = "Para recibir mensajes de otros usuarios", esCorrecta = false),
            EntidadRespuesta(idPregunta = idP4_3, textoOpcion = "Para recordarte estudiar cada día", esCorrecta = true),
            EntidadRespuesta(idPregunta = idP4_3, textoOpcion = "Para descargar lecciones", esCorrecta = false)
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

    suspend fun obtenerIntentosPorUsuario(idUsuario: Int): List<EntidadIntentoLeccion> {
        return intentoLeccionDao.obtenerIntentosPorUsuario(idUsuario)
    }
 
 
    suspend fun obtenerEstadosLogros(idUsuario: Int): EstadoLogros {
        val intentos = intentoLeccionDao.obtenerIntentosPorUsuario(idUsuario)
        val usuario = usuarioDao.obtenerUsuarioPorId(idUsuario)

        val cantidadLecciones = intentos.distinctBy { it.idLeccion }.size
        val tieneCien = intentos.any { it.calificacionObtenida == 100 }
        val rachaActual = usuario?.rachaActualDias ?: 0

        val listaLogros = mutableListOf<TipoLogro>()

        if(cantidadLecciones >= 1) listaLogros.add(TipoLogro.PRIMERA_LECCION)
        if (intentos.isNotEmpty()) listaLogros.add(TipoLogro.PRIMER_CUESTIONARIO)
        if (tieneCien) listaLogros.add(TipoLogro.NOTA_PERFECTA)
        if (cantidadLecciones >= 5) listaLogros.add(TipoLogro.COMPLETISTA)
        
        if (rachaActual >= 1) listaLogros.add(TipoLogro.RACHA_1_DIA)
        if (rachaActual >= 3) listaLogros.add(TipoLogro.RACHA_3_DIAS)
        if (rachaActual >= 7) listaLogros.add(TipoLogro.RACHA_7_DIAS)

        return EstadoLogros(logrosDesbloqueados = listaLogros)

    }

    fun programarNotificacionDiaria(
        context: Context,
        hora: Int,
        minuto: Int
    ) {
        val ahora = Calendar.getInstance()

        val objetivo = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
        }

        if (objetivo.before(ahora)) {
           objetivo.add(
               Calendar.DAY_OF_YEAR,
               1
           )
        }

        val delayEnMilisegundos = objetivo.timeInMillis - ahora.timeInMillis

        val solicitud = OneTimeWorkRequestBuilder<NotificacionWorker>().setInitialDelay(
            delayEnMilisegundos,
            TimeUnit.MILLISECONDS
        ).build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "notificacion_diaria",
                ExistingWorkPolicy.REPLACE,
                solicitud
            )
    }

    fun cancelarNotificaciones(
        context: Context
    ) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("notificacion_diaria")
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