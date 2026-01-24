package com.gabrieldev.alfabetizaciondigitalarearural.ui.principal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.ui.navegacion.Rutas
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.PantallaLecciones
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.PantallaPerfil
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.PantallaSeccionInicio

@Composable
fun PantallaPrincipal(
    usuario: EntidadUsuario,
    repositorio: RepositorioUsuario
) {
    val navController = rememberNavController()
    // Para saber qué botón destcar, observamos la ruta actual
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = rutaActual == Rutas.Inicio.ruta,
                    onClick = {
                        navController.navigate(Rutas.Inicio.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Lecciones") },
                    label = { Text("Lecciones") },
                    selected = rutaActual == Rutas.Lecciones.ruta,
                    onClick = {
                        navController.navigate(Rutas.Lecciones.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = rutaActual == Rutas.Perfil.ruta,
                    onClick = {
                        navController.navigate(Rutas.Perfil.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Rutas.Inicio.ruta
            ) {
                composable(Rutas.Inicio.ruta) {
                    PantallaSeccionInicio(usuario = usuario)
                }
                composable(Rutas.Lecciones.ruta) {
                    PantallaLecciones()
                }
                composable(Rutas.Perfil.ruta) {
                    PantallaPerfil()
                }
            }
        }
    }
}