package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import ComponenteTarjeta
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp

@Composable
fun CarruselTarjetas(
    idLeccion: Int,
    repositorio: RepositorioApp,
    onNavigateBack: () -> Unit
) {
    val tarjetas = produceState(initialValue = emptyList<EntidadTarjeta>()) {
        value = repositorio.obtenerTarjetasPorLeccion(idLeccion)
    }
    val pagerState = rememberPagerState(pageCount = { tarjetas.value.size })
    val tomarExamen = remember { androidx.compose.runtime.mutableStateOf(false) }

    val usuarioState = repositorio.ultimoUsuario.collectAsState(initial = null)
    val usuario = usuarioState.value

    if (tomarExamen.value && usuario != null) {
        PantallaCuestionario(
            idLeccion = idLeccion,
            idUsuario = usuario.idUsuario,
            repositorio = repositorio,
            onNavigateBack = {
                tomarExamen.value = false
                onNavigateBack()
            }
        )
    } else {
        Scaffold (
            topBar = {
                Surface(
                    shadowElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                        Text(
                            text = "Tarjetas de la Lección",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (tarjetas.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Cargando tarjetas o lección vacía...")
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val tarjeta = tarjetas.value[page]
                        ComponenteTarjeta(
                            tarjeta = tarjeta
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tarjeta ${pagerState.currentPage + 1} de ${tarjetas.value.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (pagerState.currentPage == tarjetas.value.size - 1) {

                            Spacer(modifier = Modifier.padding(8.dp))

                            Button (
                                onClick = { tomarExamen.value = true },
                                modifier = Modifier.padding(horizontal = 32.dp)
                            ) {
                                Text("Comenzar Evaluación")
                            }
                        }
                    }
                }
            }
        }
    }
}