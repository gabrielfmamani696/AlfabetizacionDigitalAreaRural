package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.perfilpersonal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DialogoCambiarPerfil(
    usuarioActual: EntidadUsuario,
    todosLosUsuarios: List<EntidadUsuario>,
    onDismiss: () -> Unit,
    onSeleccionar: (EntidadUsuario) -> Unit,
    onEditar: (EntidadUsuario) -> Unit,
    onEliminar: (EntidadUsuario) -> Unit
) {
    var usuarioSeleccionado by remember { mutableStateOf(usuarioActual) }

    AlertDialog (
        onDismissRequest = onDismiss,
        title = { Text("Selecciona un perfil") },
        text = {
            Column {
                if(todosLosUsuarios.size == 1) {
                    Text(
                        text = "No hay otros perfiles disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    todosLosUsuarios.forEach { usuario ->

                        ItemUsuario(
                            usuario = usuario,
                            esActual = usuario.idUsuario == usuarioActual.idUsuario,
                            seleccionado = usuario.idUsuario == usuarioSeleccionado.idUsuario,
                            onClick = { usuarioSeleccionado = usuario },
                            onEditar = { onEditar(usuario) },
                            onEliminar = { onEliminar(usuario) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (usuarioSeleccionado.idUsuario != usuarioActual.idUsuario) {
                        onSeleccionar(usuarioSeleccionado)
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ItemUsuario (
    usuario: EntidadUsuario,
    esActual: Boolean,
    seleccionado: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = seleccionado,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        //info del usuario
        Column (modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = usuario.alias,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (esActual) FontWeight.Bold else FontWeight.Normal
                )

                if (esActual) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✓ Activo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = formatearUltimaActividad(usuario.ultimaActividad),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onEditar) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(onClick = onEliminar) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun formatearUltimaActividad(timestamp: Long): String {
    val ahora = System.currentTimeMillis()
    val diferencia = ahora - timestamp

    val segundos = diferencia / 1000
    val minutos = segundos / 60
    val horas = minutos / 60
    val dias = horas / 24

    return when {
        minutos < 1 -> "Ahora mismo"
        minutos < 60 -> "Hace $minutos minuto${if (minutos > 1) "s" else ""}"
        horas < 24 -> "Hace $horas hora${if (horas > 1) "s" else ""}"
        dias < 7 -> "Hace $dias día${if (dias > 1) "s" else ""}"
        else -> {

            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.format(Date(timestamp))
        }
    }
}
