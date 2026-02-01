package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.remote.ManejadorNearby
import androidx.compose.foundation.lazy.items

@Composable
fun DialogoCompartir(
    manejador: ManejadorNearby,
    esEmisor: Boolean, // True = Enviar False = Recibir
    nombreUsuario: String,
    onDismiss: () -> Unit,
    onDispositivoSeleccionado: (String) -> Unit = {} // Solo para emisor
) {
    val estado by manejador.estadoConexion.collectAsState()
    val dispositivos by manejador.dispositivosEncontrados.collectAsState()

    LaunchedEffect (Unit) {
        if (esEmisor) {
            manejador.iniciarDescubrimiento()
        } else {
            manejador.hacerVisible(nombreUsuario)
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            if (esEmisor) Text("Buscar Dispositivo")
            else Text("Esperando Recibir...")
        },
        text = {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (esEmisor) Icons.Default.Radar else Icons.Default.Devices,
                    contentDescription = null,
                    modifier = Modifier.height(48.dp).padding(bottom = 16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = estado,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (esEmisor) {
                    HorizontalDivider()
                    Text(
                        "Dispositivos Cercanos:",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (dispositivos.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn (modifier = Modifier.height(150.dp)) {
                            items(dispositivos) { dispositivo ->
                                ListItem(
                                    headlineContent = { Text(dispositivo.nombre) },
                                    leadingContent = { Icon(Icons.Default.Devices, null) },
                                    modifier = Modifier.clickable{ onDispositivoSeleccionado(dispositivo.id) }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                } else {
                    CircularProgressIndicator()
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton (onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}