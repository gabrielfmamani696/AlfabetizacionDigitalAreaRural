package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo

@Composable
fun PantallaLecciones() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Lecciones",
            fontSize = Inclusivo.TAMANO_TEXTO_TITULO
        )
    }
}