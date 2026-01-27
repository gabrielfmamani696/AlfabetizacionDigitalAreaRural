package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo
import com.gabrieldev.alfabetizaciondigitalarearural.ui.navegacion.Rutas

@Composable
fun PantallaLecciones(
    repositorio: RepositorioUsuario,
    navController: NavController,
) {
    // Estado para la búsqueda
    var textoBusqueda by remember { mutableStateOf("") }

    // Obtenemos las lecciones del repositorio
    val listaLecciones by produceState<List<EntidadLeccion>>(initialValue = emptyList()) {
        value = repositorio.obtenerLecciones()
    }

    val leccionesFiltradas = if (textoBusqueda.isBlank()) {
        listaLecciones
    } else {
        // filtro en memoria
        listaLecciones.filter {
            it.titulo.contains(textoBusqueda, ignoreCase = true) ||
            it.tema.contains(textoBusqueda, ignoreCase = true)
        }
    }
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(16.dp))
                FloatingActionButton(
                    onClick = { /* TODO: Navegar a pantalla de crear lección */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear nueva lección")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (leccionesFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron lecciones")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Inclusivo.ESPACIADO_ESTANDAR)
                ) {
                    items(leccionesFiltradas) { leccion ->
                        ItemLeccion(
                            leccion = leccion,
                            onClick = {
                                navController.navigate(Rutas.VisualizarTarjetas.crearRuta(leccion.idLeccion))
                            },
                            onBorrar = { /* TODO: Borrar */ }
                        )
                    }
                }
            }
        }
    }
}