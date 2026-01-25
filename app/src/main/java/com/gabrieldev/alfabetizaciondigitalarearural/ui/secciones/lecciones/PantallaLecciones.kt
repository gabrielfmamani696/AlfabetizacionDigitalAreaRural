package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo
import androidx.compose.foundation.lazy.items

@Composable
fun PantallaLecciones(
    repositorio: RepositorioUsuario
) {
    val listaLecciones = produceState<List<EntidadLeccion>>(initialValue = emptyList()) {
        value = repositorio.obtenerLecciones()
    }.value

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(Inclusivo.ESPACIADO_ESTANDAR)
    ) {
        items(listaLecciones) { leccion ->
            ItemLeccion(
                leccion = leccion,
                onClick = { /* TODO: Ir al detalle */ },
                onBorrar = { /* TODO: Borrar */ }
            )
        }
    }
}