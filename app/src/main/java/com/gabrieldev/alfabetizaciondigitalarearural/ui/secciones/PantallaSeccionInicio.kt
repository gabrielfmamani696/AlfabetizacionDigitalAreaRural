package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.modelos.TipoLogro
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSeccionInicio(
    usuario: EntidadUsuario,
    repositorio: RepositorioApp
) {
    // Estado para logros
    var proximoLogro by remember { mutableStateOf<TipoLogro?>(null) }
    var progresoLogro by remember { mutableStateOf(0f) }
    var leccionesCompletadas by remember { mutableStateOf(0) }
    var cuestionariosRespondidos by remember { mutableStateOf(0) }
    var mejorNota by remember { mutableStateOf(0) }

    // Cargar datos
    LaunchedEffect(usuario.idUsuario) {
        val estadoLogros = repositorio.obtenerEstadosLogros(usuario.idUsuario)
        val intentos = repositorio.obtenerIntentosPorUsuario(usuario.idUsuario)
        
        leccionesCompletadas = intentos.distinctBy { it.idLeccion }.size
        cuestionariosRespondidos = intentos.size
        mejorNota = intentos.maxOfOrNull { it.calificacionObtenida } ?: 0

        val todosLosLogros = TipoLogro.entries
        proximoLogro = todosLosLogros.firstOrNull { logro ->
            !estadoLogros.logrosDesbloqueados.contains(logro)
        }

        progresoLogro = when (proximoLogro) {
            TipoLogro.PRIMERA_LECCION -> if (leccionesCompletadas >= 1) 1f else 0f
            TipoLogro.PRIMER_CUESTIONARIO -> if (cuestionariosRespondidos >= 1) 1f else 0f
            TipoLogro.NOTA_PERFECTA -> (mejorNota / 100f).coerceIn(0f, 1f)
            TipoLogro.COMPLETISTA -> (leccionesCompletadas / 5f).coerceIn(0f, 1f)
            TipoLogro.RACHA_1_DIA -> (usuario.rachaActualDias / 1f).coerceIn(0f, 1f)
            TipoLogro.RACHA_3_DIAS -> (usuario.rachaActualDias / 3f).coerceIn(0f, 1f)
            TipoLogro.RACHA_7_DIAS -> (usuario.rachaActualDias / 7f).coerceIn(0f, 1f)
            null -> 0f
        }
    }

    val tipDelDia = remember {
        val tips = listOf(
            "Completa tu primera lección para desbloquear la insignia 'Primer Paso' 🎓",
            "Responde un cuestionario para obtener la insignia 'Aprendiz' 🏆",
            "Obtén un 100 en cualquier lección para conseguir 'Perfeccionista' ⭐",
            "Completa 5 lecciones distintas para desbloquear 'Estudioso' 🔥",
            "Aprende 1 día seguido para obtener la insignia 'Constante' 💪",
            "Aprende 3 días seguidos para conseguir 'Comprometido' 🎯",
            "Aprende 7 días seguidos para desbloquear 'Dedicado' 🏅",

            "Comparte lecciones sin internet usando el botón de compartir 📤"
        )
        val diaDelAnio = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        tips[diaDelAnio % tips.size]
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(Inclusivo.ESPACIADO_ESTANDAR),
        verticalArrangement = Arrangement.spacedBy(Inclusivo.ESPACIADO_ESTANDAR),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hola, ${usuario.alias} 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = Inclusivo.TAMANO_TEXTO_TITULO
                    )
                    Text(
                        text = "¡A seguir aprendiendo!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        fontSize = Inclusivo.TAMANO_TEXTO_TITULO
                    )
                }
                Spacer(modifier = Modifier.height(Inclusivo.ESPACIADO_ESTANDAR))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Racha",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${usuario.rachaActualDias} días",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
        
        // Próximo Logro
        item {
            proximoLogro?.let { logro ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = logro.icono,
                            contentDescription = "Logro",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Próximo Logro: ${logro.nombreVisible}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = logro.descripcion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            LinearProgressIndicator(
                                progress = { progresoLogro },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                            )
                            Text(
                                text = "${(progresoLogro * 100).toInt()}% completado",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } ?: run {
                // Si ya tiene todos los logros
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎉",
                            style = MaterialTheme.typography.displaySmall
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "¡Felicitaciones!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Has desbloqueado todos los logros",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        // Tip del día
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "💡 Tip del día",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tipDelDia,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}