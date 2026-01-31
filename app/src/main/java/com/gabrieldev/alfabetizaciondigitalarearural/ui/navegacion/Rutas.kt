package com.gabrieldev.alfabetizaciondigitalarearural.ui.navegacion

// Sealed class define un grupo cerrado de opciones
sealed class Rutas(val ruta: String) {
    object Inicio : Rutas("inicio")
    object Lecciones : Rutas("lecciones")
    object Perfil : Rutas("perfil")

    object VisualizarTarjetas:
        Rutas("visualizar_tarjetas/{idLeccion}") {
        //redirigido a PantallaPrincipal
        fun crearRuta(idLeccion: Int) = "visualizar_tarjetas/$idLeccion"
    }

    object CrearLeccion : Rutas("crear_leccion?idLeccion={idLeccion}") {
        // Si no pasamos ID, será 0 de otra forma, sera para editar
        fun crearRuta(idLeccion: Int = 0) = "crear_leccion?idLeccion=$idLeccion"
    }
}