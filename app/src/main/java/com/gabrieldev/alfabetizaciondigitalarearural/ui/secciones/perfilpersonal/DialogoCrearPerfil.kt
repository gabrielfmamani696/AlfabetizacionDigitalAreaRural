package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.perfilpersonal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogoCrearPerfil(
    onDismiss: () -> Unit,
    onCrear: (nombre: String, cambiarANuevo: Boolean) -> Unit
) {

    //estado de alias
    var alias by remember { mutableStateOf("") }

    var cambiarANuevo by remember { mutableStateOf(true) }

    var mensajeError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Perfil") },
        text = {
            Column {
                OutlinedTextField(
                    value = alias,
                    onValueChange = {
                        alias = it
                        mensajeError = null
                    },
                    label = { Text("Alias nuevo") },
                    placeholder = { Text("Ingresa tu nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = mensajeError != null,
                    supportingText = {
                        if (mensajeError != null) {
                            Text(
                                text = mensajeError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = cambiarANuevo,
                        onCheckedChange = { cambiarANuevo = it }
                    )
                    Text(
                        text = "Cambiar a este perfil",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validación
                    if (alias.length < 4) {
                        mensajeError = "El nombre debe tener al menos 4 caracteres."
                        return@TextButton
                    }

                    if (!alias.all { it.isLetterOrDigit() }) {
                        mensajeError = "Solo se permiten letras y números (sin espacios ni símbolos)."
                        return@TextButton
                    }

                    onCrear(alias, cambiarANuevo)
                }
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}