package com.gabrieldev.alfabetizaciondigitalarearural.ui.registro

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario
import kotlinx.coroutines.launch

@Composable
fun PantallaRegistroUsuario(
    repositorio: RepositorioUsuario,
    alTerminar: () -> Unit // Callback para navegar cuando termine
) {
    // Estado para guardar lo que escribe el usuario
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
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { 
                nombre = it 
                mensajeError = null // Limpiamos el error al escribir
            },
            label = { Text("¿Cómo te llamas?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = mensajeError != null, // Se pone rojo si hay error
            supportingText = {
                if (mensajeError != null) {
                    Text(text = mensajeError!!, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // 1. Validar longitud
                if (nombre.length < 4) {
                    mensajeError = "El nombre debe tener al menos 4 caracteres."
                    return@Button
                }
                
                // 2. Validar que sea alfanumérico (letras y números)
                if (!nombre.all { it.isLetterOrDigit() }) {
                    mensajeError = "Solo se permiten letras y números (sin espacios ni símbolos)."
                    return@Button
                }

                // 3. Si pasa todo, guardamos
                scope.launch {
                    repositorio.crearUsuario(nombre)
                    alTerminar() 
                }
            },
            enabled = nombre.isNotBlank(), 
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¡Comenzar!")
        }
    }

}