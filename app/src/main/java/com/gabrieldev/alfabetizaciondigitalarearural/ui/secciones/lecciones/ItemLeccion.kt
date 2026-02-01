package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo

@Composable
fun ItemLeccion(
    leccion: EntidadLeccion,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onBorrar: () -> Unit,
    onCompartir: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(Inclusivo.ESPACIADO_ESTANDAR)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de Portada con Coil
            if (leccion.imagenUrl != null) {
                coil.compose.AsyncImage(
                    model = leccion.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(android.R.drawable.ic_menu_report_image)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "IMG",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(Inclusivo.ESPACIADO_ESTANDAR))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = leccion.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = Inclusivo.TAMANO_TEXTO_TITULO,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = leccion.tema,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = Inclusivo.TAMANO_TEXTO_CUERPO,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onCompartir) {
                    Icon(Icons.Default.Share, contentDescription = "Boton de Compartir")
                }
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Boton de Editar")
                }
                IconButton(onClick = onBorrar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Boton de Borrar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}