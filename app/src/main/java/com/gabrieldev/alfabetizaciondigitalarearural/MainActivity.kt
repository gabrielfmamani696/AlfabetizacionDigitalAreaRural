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
import com.gabrieldev.alfabetizaciondigitalarearural.ui.theme.AlfabetizacionDigitalAreaRuralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val baseDeDatos = BaseDeDatosApp.obtenerBaseDeDatos(applicationContext)
        val repositorio = RepositorioUsuario(baseDeDatos.usuarioDao())
        setContent {
            AlfabetizacionDigitalAreaRuralTheme {
                // "by" desenvuelve el valor automáticamente.
                // initial = null porque al principio puede que no haya nadie cargado.
                val usuarioGuardado by repositorio.ultimoUsuario.collectAsState(initial = null)

                // 2. Lógica de Navegación reactiva
                // Si 'usuarioGuardado' NO es null, significa que ya existe alguien en la BD
                if (usuarioGuardado != null) {
                    // Mostramos el alias (nombre) que guardamos en la entidad
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("¡Registro Exitoso!")
                        Text("Bienvenido al Home, ${usuarioGuardado?.alias}")
                    }
                } else {
                    PantallaRegistroUsuario(
                        repositorio = repositorio,
                        alTerminar = {}
                    )
                }
            }
        }
    }
}