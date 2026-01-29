package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.viewmodels.CrearLeccionViewModel
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.viewmodels.CrearLeccionViewModel.CrearLeccionViewModelFactory

@Composable
fun PantallaCrearLeccion(
    repositorio: RepositorioApp,
    onNavigateBack: () -> Unit
) {
    val viewModel: CrearLeccionViewModel = viewModel(
        factory = CrearLeccionViewModelFactory(repositorio)
    )

    // ESTADOS DEL VIEWMODEL
    val titulo by viewModel.titulo.collectAsState()
    val tema by viewModel.tema.collectAsState()
    val tituloCuestionario by viewModel.tituloCuestionario.collectAsState()
    val tarjetas by viewModel.listaTarjetas.collectAsState()
    val preguntas by viewModel.listaPreguntas.collectAsState()
    val mensajeUsuario by viewModel.mensajeUsuario.collectAsState()
    val navegarAtras by viewModel.navegarAtras.collectAsState()

    // ESTADO DE NAVEGACIÓN
    // 1=Datos, 2=Tarjetas, 3=Preguntas
    var pasoActual by remember { mutableStateOf(1) }
    val totalPasos = 3

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(mensajeUsuario) {
        mensajeUsuario?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
    }
    LaunchedEffect(navegarAtras) {
        if (navegarAtras) onNavigateBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar")
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Crear Lección",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Paso $pasoActual de $totalPasos",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        bottomBar = {
            // BARRA DE NAVEGACIÓN INFERIOR
            BottomAppBar {

                if (pasoActual > 1) {
                    TextButton(onClick = { pasoActual-- }) { Text("Anterior") }
                } else {
                    Spacer(Modifier.width(8.dp))
                }
                
                Spacer(Modifier.weight(1f))

                if (pasoActual < totalPasos) {
                    Button(onClick = { pasoActual++ }) { Text("Siguiente") }
                } else {
                    Button(onClick = { viewModel.guardarLeccion() }) { Text("Guardar Todo") }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            when (pasoActual) {
                1 -> PasoUnoDatos(
                    titulo = titulo, onTituloChange = { viewModel.actualizarTitulo(it) },
                    tema = tema, onTemaChange = { viewModel.actualizarTema(it) },
                    tituloQuiz = tituloCuestionario, onTituloQuizChange = { viewModel.actualizarTituloCuestionario(it) }
                )
                2 -> PasoDosTarjetas(
                    listaTarjetas = tarjetas,
                    onAgregar = { t, tipo, data -> viewModel.agregarTarjeta(t, tipo, data) },
                    onEliminar = { viewModel.eliminarTarjeta(it) }
                )
                3 -> PasoTresPreguntas(
                    listaPreguntas = preguntas,
                    onAgregar = { e, r -> viewModel.agregarPregunta(e, r) },
                    onEliminar = { viewModel.eliminarPregunta(it) }
                )
            }
        }
    }
}

@Composable
fun PasoUnoDatos(
    titulo: String, onTituloChange: (String) -> Unit,
    tema: String, onTemaChange: (String) -> Unit,
    tituloQuiz: String, onTituloQuizChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Información General", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        
        OutlinedTextField(
            value = titulo, onValueChange = onTituloChange,
            label = { Text("Título de la Lección") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = tema, onValueChange = onTemaChange,
            label = { Text("Tema (ej. Informática Básica)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        
        Text("Configuración del Cuestionario", style = MaterialTheme.typography.titleMedium)
        Text("Se creará automáticamente al final de la lección.", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = tituloQuiz, onValueChange = onTituloQuizChange,
            label = { Text("Título del Cuestionario (Opcional)") },
            placeholder = { Text("Ej. Examen de... (Por defecto usa el título de la lección)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasoDosTarjetas(
    listaTarjetas: List<com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta>,
    onAgregar: (String, String, String) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var contenido by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val colores = listOf(
        "#2196F3" to Color(0xFF2196F3), // Azul
        "#4CAF50" to Color(0xFF4CAF50), // Verde
        "#FF9800" to Color(0xFFFF9800), // Naranja
        "#9C27B0" to Color(0xFF9C27B0), // Púrpura
        "#F44336" to Color(0xFFF44336), // Rojo
        "#00BCD4" to Color(0xFF00BCD4)  // Cian
    )
    var colorSeleccionado by remember { mutableStateOf(colores[0].first) }
    
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imagenUri = uri
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Añadir Tarjetas de Contenido", style = MaterialTheme.typography.headlineSmall)

        // Usamos weight(0f) para que esta parte no se expanda, solo ocupe lo necesario
        Column(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nueva Tarjeta", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp) // Reducido un poco para dar espacio
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray.copy(alpha = 0.3f))
                            .clickable {
                                launcher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenUri != null) {
                            coil.compose.AsyncImage(
                                model = imagenUri,
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imagenUri = null },
                                modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha=0.5f), androidx.compose.foundation.shape.CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Image, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                                Text("Toca para imagen", color = Color.Gray)
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))

                    if (imagenUri == null) {
                        Text("Color de Fondo:", style = MaterialTheme.typography.bodySmall)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            colores.forEach { (hex, color) ->
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(color)
                                        .clickable { colorSeleccionado = hex }
                                        .then(
                                            if (colorSeleccionado == hex) 
                                                Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, androidx.compose.foundation.shape.CircleShape)
                                            else Modifier
                                        )
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = contenido, onValueChange = { contenido = it },
                        label = { Text("Texto de la tarjeta") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                    Spacer(Modifier.height(8.dp))
                    
                    Button(
                        onClick = { 
                            if (contenido.isNotBlank()) {
                                val tipo = if (imagenUri != null) "IMAGEN" else "COLOR_SOLIDO"
                                val data = if (imagenUri != null) imagenUri.toString() else colorSeleccionado
                                
                                onAgregar(contenido, tipo, data)
                                
                                contenido = ""
                                imagenUri = null
                                // Mantenemos el color seleccionado
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Text("Añadir")
                    }
                }
            }
            HorizontalDivider()
        }

        Text("Tarjetas creadas (${listaTarjetas.size}):", modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp) // Padding extra al final
        ) {
            itemsIndexed(listaTarjetas) { index, tarjeta ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(8.dp), 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                         if (tarjeta.tipoFondo == "IMAGEN") {
                            coil.compose.AsyncImage(
                                model = tarjeta.dataFondo,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            val colorFondo = try {
                                Color(android.graphics.Color.parseColor(tarjeta.dataFondo))
                            } catch (e: Exception) { Color.Gray }
                            
                            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)).background(colorFondo))
                        }
                        
                        Spacer(Modifier.width(8.dp))
                        Text(tarjeta.contenidoTexto, maxLines = 1, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onEliminar(index) }) {
                            Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PasoTresPreguntas(
    listaPreguntas: List<com.gabrieldev.alfabetizaciondigitalarearural.data.repository.PreguntaConRespuestas>,
    onAgregar: (String, List<EntidadRespuesta>) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Evaluación", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, null)
                Text(" Nueva Pregunta")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        if (listaPreguntas.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No has añadido preguntas aún.", color = Color.Gray)
            }
        }
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(listaPreguntas) { index, paquete ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("P${index+1}", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.width(8.dp))
                            Text(paquete.pregunta.enunciado, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onEliminar(index) }) {
                                Icon(Icons.Default.Delete, "Borrar")
                            }
                        }
                        // Mostrar respuestas chiquitas
                        paquete.respuestas.forEach { resp ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if(resp.esCorrecta) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if(resp.esCorrecta) Color.Green else Color.Gray
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(resp.textoOpcion, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoCrearPregunta(
            onDismiss = { mostrarDialogo = false },
            onConfirm = { e, r -> 
                onAgregar(e, r)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
fun DialogoCrearPregunta(
    onDismiss: () -> Unit,
    onConfirm: (String, List<EntidadRespuesta>) -> Unit
) {
    var enunciado by remember { mutableStateOf("") }
    var op1 by remember { mutableStateOf("") }
    var op2 by remember { mutableStateOf("") }
    var op3 by remember { mutableStateOf("") }
    var correctaIndex by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Pregunta") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = enunciado, onValueChange = { enunciado = it },
                    label = { Text("Pregunta") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text("Opciones (marca la correcta):")
                
                listOf(0, 1, 2).forEach { index ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = correctaIndex == index, onClick = { correctaIndex = index })
                        OutlinedTextField(
                            value = if(index==0) op1 else if(index==1) op2 else op3,
                            onValueChange = { 
                                if(index==0) op1=it else if(index==1) op2=it else op3=it 
                            },
                            placeholder = { Text("Opción ${index+1}") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (enunciado.isNotBlank() && op1.isNotBlank() && op2.isNotBlank() && op3.isNotBlank()) {
                    val respuestas = listOf(
                        EntidadRespuesta(idPregunta=0, textoOpcion=op1, esCorrecta=(correctaIndex==0)),
                        EntidadRespuesta(idPregunta=0, textoOpcion=op2, esCorrecta=(correctaIndex==1)),
                        EntidadRespuesta(idPregunta=0, textoOpcion=op3, esCorrecta=(correctaIndex==2))
                    )
                    onConfirm(enunciado, respuestas)
                }
            }) { Text("Añadir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
