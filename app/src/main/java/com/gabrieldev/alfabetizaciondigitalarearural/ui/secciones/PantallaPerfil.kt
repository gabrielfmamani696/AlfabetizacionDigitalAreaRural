package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp

@Composable
fun PantallaPerfil(
    usuario: EntidadUsuario,
    repositorio: RepositorioApp
) {
    //VARIABLES
    var leccionesRealizadas by remember { mutableStateOf<List<EntidadLeccion>>(emptyList()) }

    var promedios by remember { mutableStateOf<Map<Int, Double>>(emptyMap()) }

    //switch mostrar lista
    var mostrarCalificaciones by remember { mutableStateOf(false) }

    LaunchedEffect(
        usuario.idUsuario
    ) {
        val lecciones = repositorio.obtenerLeccionesRealizadasPorUsuario(usuario.idUsuario)
        leccionesRealizadas = lecciones

        //mapear leccion con su promedio
        val mapaPromedios = mutableMapOf<Int, Double>()
        lecciones.forEach { leccion ->
            val prom = repositorio.obtenerPromedioDeLeccion(usuario.idUsuario, leccion.idLeccion)
            mapaPromedios[leccion.idLeccion] = prom
        }
        promedios = mapaPromedios
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = "Perfil de ${usuario.alias}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { mostrarCalificaciones = !mostrarCalificaciones },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text( if(mostrarCalificaciones) "Ocultar Promedios" else "Ver Mi Promedio Por Lección")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mostrarCalificaciones) {
            if (leccionesRealizadas.isEmpty()) {
                Text(
                    "Aún no has completado ninguna lección",
                    color = Color.Gray
                )
            } else {
                //al presionar el boton anadiremos cargaremos informacion de las lecciones y sus promedios
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(leccionesRealizadas) { leccion ->
                        val promedio = promedios[leccion.idLeccion] ?: 0.0
                        ItemCalificacion(
                            leccion.titulo,
                            promedio
                        )
                    }
                }
            }
        }
    }
}

//recuadros de notas
@SuppressLint("DefaultLocale")
@Composable
fun ItemCalificacion(
    titulo: String,
    promedio: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //info leccion
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            //info nota
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%.1f", promedio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (promedio >= 60) Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}