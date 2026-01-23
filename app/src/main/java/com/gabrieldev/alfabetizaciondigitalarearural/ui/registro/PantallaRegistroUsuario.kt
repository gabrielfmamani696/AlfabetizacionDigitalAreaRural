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
//    guarda nombre en memoria, para q no se borre al recargar elmentos de UI, mutable es el observador
    var nombre by remember { mutableStateOf("") }
//    corrutina, dara un espacio para que se realice una accion que tomara su tiempo, dependiendo de que accion sea
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Bienvenido a tu Aventura de Alfabetizacion Digital!, para iniciar, ingresa tu nombre o un alias de al menos 4 caracteres",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("¿Cómo te llamas?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank() && nombre.length >= 4) {
                    scope.launch {
                        repositorio.crearUsuario(nombre)
                        alTerminar() // Avisamos a la MainActivity que ya terminamos
                    }
                }
            },
            enabled = nombre.isNotBlank(), // Se deshabilita si está vacío
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¡Comenzar!")
        }
    }
}