package com.gabrieldev.alfabetizaciondigitalarearural

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.BaseDeDatosApp
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import com.gabrieldev.alfabetizaciondigitalarearural.ui.principal.PantallaPrincipal
import com.gabrieldev.alfabetizaciondigitalarearural.ui.registro.PantallaRegistroUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.ui.theme.AlfabetizacionDigitalAreaRuralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseDeDatos = BaseDeDatosApp.obtenerBaseDeDatos(applicationContext)
        val repositorio = RepositorioApp(
            baseDeDatos.usuarioDao(),
            baseDeDatos.leccionDao(),
            baseDeDatos.tarjetaDao(),
            baseDeDatos.cuestionarioDao(),
            baseDeDatos.intentoLeccionDao()
        )
        setContent {
            // precarga
            LaunchedEffect(Unit) {
                repositorio.defaultLeccionesIncorporacion()
            }
            AlfabetizacionDigitalAreaRuralTheme {
                //cambio de estado en base a presencia de usuario
                val usuarioGuardado by repositorio.ultimoUsuario.collectAsState(initial = null)

                if (usuarioGuardado != null) {
                    PantallaPrincipal(
                        usuario = usuarioGuardado!!,
                        repositorio = repositorio
                    )
                } else {
                    PantallaRegistroUsuario(
                        repositorio = repositorio,
                        alTerminar = {
                            // Al guardar, el Flow se actualiza solo y entrará al 'if' de arriba
                        }
                    )
                }
            }
        }
    }
}