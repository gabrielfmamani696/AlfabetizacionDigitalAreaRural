package com.gabrieldev.alfabetizaciondigitalarearural

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.BaseDeDatosApp
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.ui.registro.PantallaRegistroUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.PantallaSeccionInicio
import com.gabrieldev.alfabetizaciondigitalarearural.ui.theme.AlfabetizacionDigitalAreaRuralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val baseDeDatos = BaseDeDatosApp.obtenerBaseDeDatos(applicationContext)
        val repositorio = RepositorioUsuario(baseDeDatos.usuarioDao())

        setContent {
            AlfabetizacionDigitalAreaRuralTheme {
//              cambio de estado en base a presencia de usuario
                val usuarioGuardado by repositorio.ultimoUsuario.collectAsState(initial = null)

                if (usuarioGuardado != null) {
                    PantallaSeccionInicio(
                        usuario = usuarioGuardado!!
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