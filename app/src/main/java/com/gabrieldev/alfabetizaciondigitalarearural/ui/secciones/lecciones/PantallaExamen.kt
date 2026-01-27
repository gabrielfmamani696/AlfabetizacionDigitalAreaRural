package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario

@Composable
fun PantallaExamen(
    idLeccion: Int,
    idUsuario: Int,
    repositorio: RepositorioUsuario,
    onNavigateBack: () -> Unit
) {
    val viewModel: ExamenViewModel = viewModel(
        factory = ExamenViewModelFactory(repositorio, idLeccion, idUsuario)
    )
    // variables de estado
    val estado by viewModel.estadoExamen.collectAsState()
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
                is EstadoExamen.Cargando -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is EstadoExamen.Error -> Text("Error: ${st.mensaje}", Modifier.align(Alignment.Center))
                is EstadoExamen.Finalizado -> {
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
                is EstadoExamen.ExamenActivo -> {
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
                                Button(onClick = { viewModel.finalizarExamen() }) { Text("Finalizar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
