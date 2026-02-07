package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.perfilpersonal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.modelos.TipoLogro

/**
 * Componente que muestra una insignia individual.
 * Las insignias bloqueadas se muestran en gris y semitransparentes.
 */
@Composable
fun ItemInsignia(
    logro: TipoLogro,
    desbloqueado: Boolean,
    modifier: Modifier = Modifier
) {

    val colorFondo = if (desbloqueado) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val colorIcono = if (desbloqueado) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Gray
    }

    val transparencia = if (desbloqueado) 1f else 0.4f

    Card(
        modifier = modifier
            .size(120.dp)
            .alpha(transparencia),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (desbloqueado) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        color = colorIcono.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = logro.icono,
                    contentDescription = logro.nombreVisible,
                    tint = colorIcono,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = logro.nombreVisible,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = logro.descripcion,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}