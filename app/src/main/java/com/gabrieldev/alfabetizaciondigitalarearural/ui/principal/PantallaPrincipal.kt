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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.repository.RepositorioApp
import com.gabrieldev.alfabetizaciondigitalarearural.ui.navegacion.Rutas
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.perfilpersonal.PantallaPerfil
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.PantallaSeccionInicio
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.CarruselTarjetas
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.PantallaCrearLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.ui.secciones.lecciones.PantallaLecciones

@Composable
fun PantallaPrincipal(
    usuario: EntidadUsuario,
    repositorio: RepositorioApp
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
                    PantallaLecciones(
                        repositorio = repositorio,
                        navController = navController,
                    )
                }
                composable(Rutas.Perfil.ruta) {
                    PantallaPerfil(
                        usuario = usuario,
                        repositorio = repositorio
                    )
                }
                composable(
                    route = Rutas.VisualizarTarjetas.ruta,
                    arguments = listOf(navArgument("idLeccion") { type = NavType.IntType })
                ) { backStackEntry ->
                    val idLeccion = backStackEntry.arguments?.getInt("idLeccion") ?: 0
                    CarruselTarjetas(
                        idLeccion = idLeccion,
                        repositorio = repositorio,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Rutas.CrearLeccion.ruta,
                    arguments = listOf(
                        navArgument("idLeccion") {
                            type = NavType.IntType
                            defaultValue = 0
                        }
                    )
                ) { backStackEntry ->
                    // Recuperamos el ID que viene en la ruta
                    val idLeccion = backStackEntry.arguments?.getInt("idLeccion") ?: 0

                    // lo pasamos a la pantalla
                    PantallaCrearLeccion (
                        repositorio = repositorio,
                        onNavigateBack = { navController.popBackStack() },
                        idLeccionEditar = idLeccion
                    )
                }
            }
        }
    }
}