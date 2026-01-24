package com.gabrieldev.alfabetizaciondigitalarearural.ui.registro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo
import kotlinx.coroutines.launch

@Composable
fun PantallaRegistroUsuario(
    repositorio: RepositorioUsuario,
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