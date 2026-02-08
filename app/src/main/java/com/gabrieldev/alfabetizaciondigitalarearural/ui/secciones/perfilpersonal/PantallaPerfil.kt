package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.perfilpersonal

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.modelos.TipoLogro
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import kotlinx.coroutines.launch

@Composable
fun PantallaPerfil(
    usuario: EntidadUsuario,
    repositorio: RepositorioApp
) {
    //VARIABLES
    var leccionesRealizadas by remember { mutableStateOf<List<EntidadLeccion>>(emptyList()) }

    var promedios by remember { mutableStateOf<Map<Int, Double>>(emptyMap()) }

    //switch mostrar lista
    var mostrarCalificaciones by remember { mutableStateOf(false) }

    //logros
    var logrosConEstado by remember {
        mutableStateOf<List<Pair<TipoLogro, Boolean>>>(emptyList())
    }
    var logroNuevo by remember { mutableStateOf<TipoLogro?>(null) }

    //estados para creacion y cambio de perfil
    var mostrarDialogoCambiarPerfil by remember { mutableStateOf(false) }
    var mostrarDialogoCrearPerfil by remember { mutableStateOf(false) }
    var todosLosUsuarios by remember { mutableStateOf<List<EntidadUsuario>>(emptyList()) }

    //corrutina
    val scope = rememberCoroutineScope()

    LaunchedEffect(
        usuario.idUsuario
    ) {
        val lecciones = repositorio.obtenerLeccionesRealizadasPorUsuario(usuario.idUsuario)
        leccionesRealizadas = lecciones

        //mapear leccion con su promedio
        val mapaPromedios = mutableMapOf<Int, Double>()
        lecciones.forEach { leccion ->
            val prom = repositorio.obtenerPromedioDeLeccion(usuario.idUsuario, leccion.idLeccion)
            mapaPromedios[leccion.idLeccion] = prom
        }
        promedios = mapaPromedios

        //logros
        val estadoLogros = repositorio.obtenerEstadosLogros(usuario.idUsuario)
        logrosConEstado = estadoLogros.obtenerTodosConEstado()

        // Verificar si hay logros nuevos para mostrar alerta
        val logrosNuevos = repositorio.obtenerLogrosNuevos(usuario.idUsuario)
        if (logrosNuevos.isNotEmpty()) {
            logroNuevo = logrosNuevos.first()
        }

        todosLosUsuarios = repositorio.obtenerTodosLosUsuarios()
    }

    //logro desbloqueado
    logroNuevo?.let { logro ->
        AlertDialog(
            onDismissRequest = {
                // Marcar como notificado y cerrar
                scope.launch {
                    repositorio.marcarLogroComoNotificado(usuario.idUsuario, logro)
                }
                logroNuevo = null
            },
            icon = {
                Icon(
                    imageVector = logro.icono,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text("¡Logro Desbloqueado!")
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = logro.nombreVisible,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = logro.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton (
                    onClick = {
                        scope.launch {
                            repositorio.marcarLogroComoNotificado(usuario.idUsuario, logro)
                        }
                        logroNuevo = null
                    }
                ) {
                    Text("¡Genial!")
                }
            }
        )
    }

    // cambiar perfil
    if (mostrarDialogoCambiarPerfil) {
        DialogoCambiarPerfil(
            usuarioActual = usuario,
            todosLosUsuarios = todosLosUsuarios,
            onDismiss = { mostrarDialogoCambiarPerfil = false },
            onSeleccionar = { usuarioSeleccionado ->
                scope.launch {
                    repositorio.cambiarUsuarioActivo(usuarioSeleccionado.idUsuario)
                    mostrarDialogoCambiarPerfil = false
                }
            }
        )
    }

    // crear perfil
    if (mostrarDialogoCrearPerfil) {
        DialogoCrearPerfil(
            onDismiss = { mostrarDialogoCrearPerfil = false },
            onCrear = { nombre, cambiarANuevo ->
                scope.launch {
                    repositorio.crearUsuario(nombre)

                    //recargar
                    todosLosUsuarios = repositorio.obtenerTodosLosUsuarios()

                    if (cambiarANuevo) {

                        val nuevoUsuario = todosLosUsuarios.find { it.alias == nombre }
                        nuevoUsuario?.let {
                            repositorio.cambiarUsuarioActivo(it.idUsuario)
                        }
                    }

                    mostrarDialogoCrearPerfil = false
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Perfil de ${usuario.alias}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sección de Gestión de Perfiles
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "👤 Gestión de Perfiles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Cambiar Perfil
                OutlinedButton (
                    onClick = { mostrarDialogoCambiarPerfil = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Perfil")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón Crear Nuevo Perfil
                OutlinedButton(
                    onClick = { mostrarDialogoCrearPerfil = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crear Nuevo Perfil")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // lsita de Logros
        item {
            Text(
                text = "Mis Logros",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            val filas = (logrosConEstado.size + 1) / 2
            val alturaGrid = (filas * 140).dp

            LazyVerticalGrid (
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaGrid),
                userScrollEnabled = false
            ) {
                items(logrosConEstado) { (logro, desbloqueado) ->
                    ItemInsignia(
                        logro = logro,
                        desbloqueado = desbloqueado
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = { mostrarCalificaciones = !mostrarCalificaciones },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (mostrarCalificaciones) "Ocultar Promedios"
                    else "Ver Mi Promedio Por Lección"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de calificaciones (existente, adaptado)
        if (mostrarCalificaciones) {
            if (leccionesRealizadas.isEmpty()) {
                item {
                    Text(
                        "Aún no has completado ninguna lección",
                        color = Color.Gray
                    )
                }
            } else {
                items(leccionesRealizadas) { leccion ->
                    val promedio = promedios[leccion.idLeccion] ?: 0.0
                    ItemCalificacion(leccion.titulo, promedio)
                }
            }
        }
    }
}

//recuadros de notas
@SuppressLint("DefaultLocale")
@Composable
fun ItemCalificacion(
    titulo: String,
    promedio: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //info leccion
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            //info nota
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%.1f", promedio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (promedio >= 60) Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}