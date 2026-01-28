package com.gabrieldev.alfabetizaciondigitalarearural.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo
import kotlinx.coroutines.launch

@Composable
fun PantallaRegistroUsuario(
    repositorio: RepositorioApp,
    alTerminar: () -> Unit
) {
    // Estado para guardar lo el alias del usuario
    var nombre by remember { mutableStateOf("") }
    // Estado para mostrar errores de validación
    var mensajeError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Bienvenido a tu Aventura de Alfabetización Digital!",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Para iniciar, ingresa tu nombre (mínimo 4 letras y/ó números).",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
            fontSize = Inclusivo.WTS
        )
        Spacer(modifier = Modifier.height(Inclusivo.ESPACIADO_ESTANDAR))
        OutlinedTextField(
            value = nombre,
            onValueChange = { 
                nombre = it 
                mensajeError = null
            },
            label = { Text("¿Cómo te llamas, o cual es el Alias por el cual quieres que te tratemos?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = mensajeError != null,
            supportingText = {
                if (mensajeError != null) {
                    Text(text = mensajeError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(Inclusivo.ESPACIADO_ESTANDAR))
        Button(
            onClick = {
                if (nombre.length < 4) {
                    mensajeError = "El nombre debe tener al menos 4 caracteres."
                    return@Button
                }
                if (!nombre.all { it.isLetterOrDigit() }) {
                    mensajeError = "Solo se permiten letras y números (sin espacios ni símbolos)."
                    return@Button
                }
                scope.launch {
                    repositorio.crearUsuario(nombre)
                    alTerminar() 
                }
            },
            enabled = nombre.isNotBlank(), 
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Inclusivo.TAM_BOTON_MIN),
        ) {
            Text("¡Comenzar a crear Lecciones!")
        }
    }
}