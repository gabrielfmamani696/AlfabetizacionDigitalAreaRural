package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp

@Composable
fun PantallaCuestionario(
    idLeccion: Int,
    idUsuario: Int,
    repositorio: RepositorioApp,
    onNavigateBack: () -> Unit
) {
    val viewModel: CuestionarioViewModel = viewModel(
        factory = CuestionarioViewModelFactory(repositorio, idLeccion, idUsuario)
    )
    // variables de estado
    val estado by viewModel.estadoCuestionario.collectAsState()
    val indiceActual by viewModel.indicePreguntaActual.collectAsState()
    val respuestas by viewModel.respuestasUsuario.collectAsState()

    Scaffold(
        topBar = {
             Surface(shadowElevation = 4.dp) {
                 Row(
                     modifier = Modifier.fillMaxWidth().padding(16.dp),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     Text("Evaluación", style = MaterialTheme.typography.titleLarge)
                 }
             }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val st = estado) {
                is EstadoCuestionario.Cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is EstadoCuestionario.Error -> Text("Error: ${st.mensaje}", Modifier.align(Alignment.Center))
                is EstadoCuestionario.Finalizado -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Examen Finalizado", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (st.aprobado) "¡Aprobado! 🎉" else "Inténtalo de nuevo",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (st.aprobado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(text = "Nota: ${st.nota}/100", style = MaterialTheme.typography.headlineLarge)
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = onNavigateBack) { Text("Volver a Lecciones") }
                    }
                }
                is EstadoCuestionario.CuestionarioActivo -> {
                    val preguntaActual = st.datos.preguntas[indiceActual]
                    Column(Modifier.padding(16.dp)) {
                        LinearProgressIndicator(
                            progress = { (indiceActual + 1).toFloat() / st.datos.preguntas.size },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Pregunta ${indiceActual + 1} de ${st.datos.preguntas.size}",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = preguntaActual.pregunta.enunciado,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(24.dp))
                        // Opciones
                        Column {
                            preguntaActual.respuestas.forEach { respuesta ->
                                val selected = respuestas[preguntaActual.pregunta.idPregunta] == respuesta.idRespuesta
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .selectable(
                                            selected = selected,
                                            onClick = { viewModel.seleccionarRespuesta(preguntaActual.pregunta.idPregunta, respuesta.idRespuesta) }
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selected,
                                            onClick = null 
                                        )
                                        Text(
                                            text = respuesta.textoOpcion, 
                                            modifier = Modifier.padding(start = 8.dp),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            if (indiceActual > 0) {
                                OutlinedButton(onClick = { viewModel.anteriorPregunta() }) { Text("Anterior") }
                            } else { Spacer(Modifier.width(1.dp)) }
                            if (indiceActual < st.datos.preguntas.size - 1) {
                                Button(onClick = { viewModel.siguientePregunta() }) { Text("Siguiente") }
                            } else {
                                Button(onClick = { viewModel.finalizarCuestionario() }) { Text("Finalizar") }
                            }
                        }
                    }
                }
            }
        }
    }
}