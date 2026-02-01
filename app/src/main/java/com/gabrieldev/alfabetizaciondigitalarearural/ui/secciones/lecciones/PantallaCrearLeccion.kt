package com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.viewmodels.CrearLeccionViewModel
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.viewmodels.CrearLeccionViewModel.CrearLeccionViewModelFactory

@Composable
fun PantallaCrearLeccion(
    repositorio: RepositorioApp,
    onNavigateBack: () -> Unit,
    idLeccionEditar: Int = 0
) {
    val viewModel: CrearLeccionViewModel = viewModel(
        factory = CrearLeccionViewModelFactory(repositorio)
    )

    // ejecutado una vez al entrar a la pantalla
    LaunchedEffect(Unit) {
        if (idLeccionEditar > 0) {
            viewModel.cargarDatosParaEdicion(idLeccionEditar)
        }
    }

    // ESTADOS DEL VIEWMODEL
    val titulo by viewModel.titulo.collectAsState()
    val tema by viewModel.tema.collectAsState()
    
    // Ahora observamos la lista de cuestionarios y el activo
    val listaCuestionarios by viewModel.listaCuestionarios.collectAsState()
    val cuestionarioActivoId by viewModel.cuestionarioActivoId.collectAsState()
    
    val tarjetas by viewModel.listaTarjetas.collectAsState()
    val mensajeUsuario by viewModel.mensajeUsuario.collectAsState()
    val navegarAtras by viewModel.navegarAtras.collectAsState()

    // ESTADO DE NAVEGACIÓN
    // 1=Datos, 2=Tarjetas, 3=Cuestionarios
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
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(onClick = { viewModel.guardarLeccion(context) }) { Text("Guardar Todo") }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            when (pasoActual) {
                1 -> {
                    // Obtenemos el estado de la imagen de portada
                    val imagenPortada by viewModel.imagenPortada.collectAsState()
                    Lecciones(
                        titulo = titulo, onTituloChange = { viewModel.actualizarTitulo(it) },
                        tema = tema, onTemaChange = { viewModel.actualizarTema(it) },
                        imagenPortada = imagenPortada,
                        onImagenPortadaChange = { uri -> viewModel.actualizarImagenPortada(uri) }
                    )
                }
                2 -> Tarjetas(
                    listaTarjetas = tarjetas,
                    onAgregar = { t, tipo, data -> viewModel.agregarTarjeta(t, tipo, data) },
                    onEliminar = { viewModel.eliminarTarjeta(it) }
                )
                3 -> Cuestionarios(
                    listaCuestionarios = listaCuestionarios,
                    cuestionarioActivoId = cuestionarioActivoId,
                    onCrearCuestionario = { title -> viewModel.crearNuevoCuestionario(title) },
                    onSeleccionarCuestionario = { id -> viewModel.seleccionarCuestionario(id) },
                    onEliminarCuestionario = { id -> viewModel.eliminarCuestionario(id) },
                    onAgregarPregunta = { e, r -> viewModel.agregarPregunta(e, r) },
                    onEliminarPregunta = { index -> viewModel.eliminarPregunta(index) },
                    onTituloChange = { titulo -> viewModel.actualizarTituloCuestionario(titulo) }
                )
            }
        }
    }
}

@Composable
fun Lecciones(
    titulo: String, onTituloChange: (String) -> Unit,
    tema: String, onTemaChange: (String) -> Unit,
    imagenPortada: String?,
    onImagenPortadaChange: (String?) -> Unit
) {
    val launcherPortada = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        onImagenPortadaChange(uri?.toString())
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Información General", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    launcherPortada.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            contentAlignment = Alignment.Center
        ) {
            if (imagenPortada != null) {
                coil.compose.AsyncImage(
                    model = imagenPortada,
                    contentDescription = "Portada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Botón para quitar imagen
                IconButton(
                    onClick = { onImagenPortadaChange(null) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha=0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.White)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image, 
                        contentDescription = null, 
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Toca para agregar Portada", 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

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
    }
}

@Composable
fun Tarjetas(
    listaTarjetas: List<EntidadTarjeta>,
    onAgregar: (String, String, String) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var contenido by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    val colores = listOf(
        "#2196F3" to Color(0xFF2196F3),
        "#4CAF50" to Color(0xFF4CAF50),
        "#FF9800" to Color(0xFFFF9800),
        "#9C27B0" to Color(0xFF9C27B0),
        "#F44336" to Color(0xFFF44336),
        "#00BCD4" to Color(0xFF00BCD4)
    )
    var colorSeleccionado by remember { mutableStateOf(colores[0].first) }
    
    val launcher = rememberLauncherForActivityResult (
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imagenUri = uri
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text("Añadir Tarjetas de Contenido", style = MaterialTheme.typography.headlineSmall)
            
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
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray.copy(alpha = 0.3f))
                            .clickable {
                                launcher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imagenUri != null) {
                            coil.compose.AsyncImage(
                                model = imagenUri,
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { imagenUri = null },
                                modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha=0.5f), CircleShape
                                )
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
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { colorSeleccionado = hex }
                                        .then(
                                            if (colorSeleccionado == hex) 
                                                Modifier.border(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.onSurface,
                                                    CircleShape)
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
            Text("Tarjetas creadas (${listaTarjetas.size}):", modifier = Modifier.padding(vertical = 8.dp))
        }

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
                            contentScale = ContentScale.Crop
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

@Composable
fun Cuestionarios(
    listaCuestionarios: List<CrearLeccionViewModel.CuestionarioBorrador>,
    cuestionarioActivoId: String?,
    onCrearCuestionario: (String) -> Unit,
    onSeleccionarCuestionario: (String?) -> Unit,
    onEliminarCuestionario: (String) -> Unit,
    onAgregarPregunta: (String, List<EntidadRespuesta>) -> Unit,
    onEliminarPregunta: (Int) -> Unit,
    onTituloChange: (String) -> Unit
) {
    if (cuestionarioActivoId == null) {
        VistaListaCuestionarios(
            lista = listaCuestionarios,
            onCrear = onCrearCuestionario,
            onSeleccionar = onSeleccionarCuestionario,
            onEliminar = onEliminarCuestionario
        )
    } else {
        val activo = listaCuestionarios.find { it.idTemporal == cuestionarioActivoId }
        if (activo != null) {
            VistaEditorPreguntas(
                cuestionario = activo,
                onVolver = { onSeleccionarCuestionario(null) },
                onAgregar = onAgregarPregunta,
                onEliminar = onEliminarPregunta,
                onTituloChange = onTituloChange
            )
        } else {
            LaunchedEffect(Unit) { onSeleccionarCuestionario(null) }
        }
    }
}

@Composable
fun VistaListaCuestionarios(
    lista: List<CrearLeccionViewModel.CuestionarioBorrador>,
    onCrear: (String) -> Unit,
    onSeleccionar: (String) -> Unit,
    onEliminar: (String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nuevoTitulo by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Evaluaciones", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, null)
                Text(" Nuevo Cuestionario")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        if (lista.isEmpty()) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    "No hay cuestionarios creados.\nAgrega uno para evaluar a los estudiantes.", 
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(lista) { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSeleccionar(item.idTemporal) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.titulo, style = MaterialTheme.typography.titleMedium)
                                Text("${item.preguntas.size} preguntas", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { onSeleccionar(item.idTemporal) }) {
                                Icon(Icons.Default.Edit, "Editar")
                            }
                            IconButton(onClick = { onEliminar(item.idTemporal) }) {
                                Icon(Icons.Default.Delete, "Borrar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nuevo Cuestionario") },
            text = {
                OutlinedTextField(
                    value = nuevoTitulo,
                    onValueChange = { nuevoTitulo = it },
                    label = { Text("Título (ej. Examen Final)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { 
                    if (nuevoTitulo.isNotBlank()) {
                        onCrear(nuevoTitulo)
                        nuevoTitulo = ""
                        mostrarDialogo = false
                    }
                }) { Text("Crear") }
            },
            dismissButton = { 
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") } 
            }
        )
    }
}

@Composable
fun VistaEditorPreguntas(
    cuestionario: CrearLeccionViewModel.CuestionarioBorrador,
    onVolver: () -> Unit,
    onAgregar: (String, List<EntidadRespuesta>) -> Unit,
    onEliminar: (Int) -> Unit,
    onTituloChange: (String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
            }
            Text(
                text = cuestionario.titulo, 
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            OutlinedTextField(
                value = cuestionario.titulo,
                onValueChange = onTituloChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                label = { Text("Título del Cuestionario") },
                singleLine = true
            )
        }
        
        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${cuestionario.preguntas.size} Preguntas", style = MaterialTheme.typography.labelLarge)
            Button(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, null)
                Text(" Agregar Pregunta")
            }
        }
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(cuestionario.preguntas) { index, paquete ->
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
