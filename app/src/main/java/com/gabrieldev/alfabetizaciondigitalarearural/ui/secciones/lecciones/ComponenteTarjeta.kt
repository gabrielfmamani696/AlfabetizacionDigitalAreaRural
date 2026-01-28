import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta

@Composable
fun ComponenteTarjeta(
    tarjeta: EntidadTarjeta
    ) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo
            when (tarjeta.tipoFondo) {
                "COLOR_SOLIDO" -> {
                    val color = try {
                        Color(android.graphics.Color.parseColor(tarjeta.dataFondo))
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                    )
                }
                "IMAGEN" -> {
                    val context = LocalContext.current
                    val resId = context.resources.getIdentifier(
                        tarjeta.dataFondo,
                        "drawable",
                        context.packageName
                    )
                    if (resId != 0) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )
                    } else {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text("Imagen no encontrada: ${tarjeta.dataFondo}", modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = tarjeta.contenidoTexto,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (tarjeta.tipoFondo == "COLOR_SOLIDO" || tarjeta.tipoFondo == "IMAGEN")
                        Color.White
                    else Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}