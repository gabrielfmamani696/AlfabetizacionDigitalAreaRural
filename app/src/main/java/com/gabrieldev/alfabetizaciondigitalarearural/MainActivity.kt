package com.gabrieldev.alfabetizaciondigitalarearural

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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

        crearCanalNotificacion()

        val baseDeDatos = BaseDeDatosApp.obtenerBaseDeDatos(applicationContext)
        val repositorio = RepositorioApp(
            baseDeDatos.usuarioDao(),
            baseDeDatos.leccionDao(),
            baseDeDatos.tarjetaDao(),
            baseDeDatos.cuestionarioDao(),
            baseDeDatos.intentoLeccionDao(),
            baseDeDatos.logroNotificadoDao()
        )
        setContent {
            // precarga
            LaunchedEffect(Unit) {
                repositorio.defaultLeccionesIncorporacion()
            }
            AlfabetizacionDigitalAreaRuralTheme {
                //cambio de estado en base a presencia de usuario
                val usuarioGuardado by repositorio.usuarioActivo.collectAsState(initial = null)

                if (usuarioGuardado != null) {
                    PantallaPrincipal(
                        usuario = usuarioGuardado!!,
                        repositorio = repositorio
                    )
                } else {
                    PantallaRegistroUsuario(
                        repositorio = repositorio,
                        alTerminar = {
                        }
                    )
                }
            }
        }
    }

    private fun crearCanalNotificacion() {
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Recordatorio de Aprendizaje"
            val descripcion = "Canal para recordatorios diarios de estudio"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel("CANAL_ALFABETIZACION", nombre, importancia).apply {
                description = descripcion
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}