package com.gabrieldev.alfabetizaciondigitalarearural.ui.navegacion

// Sealed class define un grupo cerrado de opciones
sealed class Rutas(val ruta: String) {
    object Inicio : Rutas("inicio")
    object Lecciones : Rutas("lecciones")
    object Perfil : Rutas("perfil")
}