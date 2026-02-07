package com.gabrieldev.alfabetizaciondigitalarearural.data.local.modelos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector


enum class TipoLogro (
    val nombreVisible: String,
    val descripcion: String,
    val icono: ImageVector
) {
    PRIMERA_LECCION(
        nombreVisible = "Primer Paso",
        descripcion = "Completa tu primera lección",
        icono = Icons.Default.School
    ),
    PRIMER_CUESTIONARIO (
        nombreVisible = "Aprendiz",
        descripcion = "Responde tu primer cuestionario",
        icono = Icons.Default.EmojiEvents
    ),
    NOTA_PERFECTA (
        nombreVisible = "Perfeccionista",
        descripcion = "Obtén un 100 en cualquier lección",
        icono = Icons.Default.Star

    ),
    COMPLETISTA (
        nombreVisible = "Estudioso",
        descripcion = "Completa 5 lecciones distintas",
        icono = Icons.Default.LocalFireDepartment
    )
}

data class EstadoLogros (
    val logrosDesbloqueados: List<TipoLogro>
) {
    fun obtenerTodosConEstado(): List<Pair<TipoLogro, Boolean>> {
        return TipoLogro.entries.map { logro ->
            logro to logrosDesbloqueados.contains(logro)
        }
    }
}