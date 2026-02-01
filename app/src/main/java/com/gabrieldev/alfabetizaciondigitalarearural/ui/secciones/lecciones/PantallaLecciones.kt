package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.remote.ManejadorNearby
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import com.gabrieldev.alfabetizaciondigitalarearural.ui.Inclusivo
import com.gabrieldev.alfabetizaciondigitalarearural.ui.navegacion.Rutas
import kotlinx.coroutines.launch

@Composable
fun PantallaLecciones(
    repositorio: RepositorioApp,
    navController: NavController,
) {
    // Estado para la búsqueda
    var textoBusqueda by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    var updateTrigger by remember { mutableStateOf(0) }

    var leccionParaBorrar by remember { mutableStateOf<EntidadLeccion?>(null) }

    // para el dialogo de confirmacion
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }

    val context = LocalContext.current

    val manejador = remember { ManejadorNearby(context) }

    // para el dialogo de compartir
    var mostrarDialogoCompartir by remember { mutableStateOf(false) }
    var esModoEmisor by remember { mutableStateOf(true) }
    var leccionACompartir by remember { mutableStateOf<EntidadLeccion?>(null) } // Si es null, es modo recibir

    val permisosLauncher = rememberLauncherForActivityResult (
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permisos ->
            val todosAceptados = permisos.values.all { it }
            if (todosAceptados) {
                mostrarDialogoCompartir = true
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("No se otorgaron todos los permisos")
                }
            }
        }
    )

    //pedir permisos y abrir dialogo
    val abrirCompartir = {emisor: Boolean, leccion: EntidadLeccion? ->
        esModoEmisor = emisor
        leccionACompartir = leccion

        //permisos segun version
        val permisosRequeridos = when {
            // Android 13+ (API 33) necesita NEARBY_WIFI_DEVICES
            android.os.Build.VERSION.SDK_INT >= 33 -> {
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_ADVERTISE,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.NEARBY_WIFI_DEVICES // ¡Este faltaba!
                )
            }
            // Android 12 (API 31/32)
            android.os.Build.VERSION.SDK_INT >= 31 -> {
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_ADVERTISE,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            // Android 11 o inferior
            else -> {
                arrayOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
        permisosLauncher.launch(permisosRequeridos)
    }

    // Obtenemos las lecciones del repositorio
    val listaLecciones by produceState<List<EntidadLeccion>>(
        initialValue = emptyList(),
        key1 = updateTrigger
        )
    {
        value = repositorio.obtenerLecciones()
    }

    val leccionesFiltradas = if (textoBusqueda.isBlank()) {
        listaLecciones
    } else {
        // filtro en memoria
        listaLecciones.filter {
            it.titulo.contains(textoBusqueda, ignoreCase = true) ||
            it.tema.contains(textoBusqueda, ignoreCase = true)
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Buscar...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    singleLine = true
                )

                OutlinedButton(
                    onClick = { abrirCompartir(false, null) }
                ) {
                    Icon(
                        Icons.Default.Download,
                        null
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text("Recibir")
                }

                Spacer(modifier = Modifier.width(16.dp))

                FloatingActionButton(
                    onClick = { navController.navigate(Rutas.CrearLeccion.ruta) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear nueva lección"
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (leccionesFiltradas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron lecciones")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Inclusivo.ESPACIADO_ESTANDAR)
                ) {
                    items(leccionesFiltradas) { leccion ->
                        ItemLeccion(
                            leccion = leccion,
                            onClick = {
                                navController.navigate(Rutas.VisualizarTarjetas.crearRuta(leccion.idLeccion))
                            },
                            onEditar = {
                                navController.navigate(Rutas.CrearLeccion.crearRuta(leccion.idLeccion))
                            },
                            onBorrar = {
                                leccionParaBorrar = leccion
                            },
                            onCompartir = { //emviar
                                abrirCompartir(true, leccion)
                            }
                        )
                    }
                }
            }
        }

        //dialogo de confirmacion para la eliminacion
        leccionParaBorrar?.let { leccion ->
            AlertDialog(
                onDismissRequest = { leccionParaBorrar = null },
                title = { Text("¿Eliminar lección?") },
                text = { Text("Se eliminará '${leccion.titulo}'. Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val idParaBorrar = leccion.idLeccion

                            scope.launch {
                                try {
                                    repositorio.eliminarLeccionPorId(idParaBorrar)

                                    updateTrigger++

                                    leccionParaBorrar = null

                                    //mensaje de confirmacion
                                    snackbarHostState.showSnackbar("Lección eliminada correctamente")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { leccionParaBorrar = null }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo de Nearby Connections
        if (mostrarDialogoCompartir) {
            val usuario = repositorio.ultimoUsuario.collectAsState(initial = null).value
            val nombreBase = usuario?.alias ?: "Usuario"
            val nombreDispositivo = "${nombreBase} (${android.os.Build.MODEL})" // Ej: Gabriel (Samsung S21)

            DialogoCompartir(
                manejador = manejador,
                esEmisor = esModoEmisor,
                nombreUsuario = nombreDispositivo,
                onDismiss = {
                    mostrarDialogoCompartir = false
                    manejador.detenerTodo()
                },
                onDispositivoSeleccionado = { endpointId ->
                    if (esModoEmisor && leccionACompartir != null) {
                        scope.launch {
                            val (tarjetas, cuestionarios) = repositorio.obtenerLeccionConCuestionarios(leccionACompartir!!.idLeccion)

                            val paquete = com.gabrieldev.alfabetizaciondigitalarearural.data.remote.LeccionTransferible(
                                leccion = leccionACompartir!!,
                                tarjetas = tarjetas,
                                cuestionarios = cuestionarios
                            )
                            // Usamos la nueva función que conecta Y LUEGO envía
                            manejador.conectarYEnviar(endpointId, paquete)
                        }
                    } else {
                        manejador.conectarA(endpointId)
                    }
                }
            )

            // Efecto para escuchar cuando llega una lección
            LaunchedEffect(Unit) {
                manejador.onLeccionRecibida = { paquete ->
                    scope.launch {

                        val idNueva = repositorio.insertarLeccion(paquete.leccion.copy(
                            idLeccion = 0,
                            titulo = "${paquete.leccion.titulo} (Recibida)",
                            creadaPorUsuario = false
                        )).toInt()
                        // Guardar tarjetas
                        val tarjetasNuevas = paquete.tarjetas.map { it.copy(idTarjeta = 0, idLeccion = idNueva) }
                        tarjetasNuevas.forEach { repositorio.insertarTarjeta(it) }
                        // Guardar cuestionarios
                        paquete.cuestionarios.forEach { q ->
                            val nuevoCuestionario = q.cuestionario.copy(idCuestionario = 0, idLeccion = idNueva)
                            repositorio.insertarCuestionarioCompleto(nuevoCuestionario, q.preguntas)
                        }
                        mostrarDialogoCompartir = false
                        manejador.detenerTodo()

                        snackbarHostState.showSnackbar("¡Lección recibida e importada con éxito!")
                        updateTrigger++
                    }
                }
            }
        }
    }
}