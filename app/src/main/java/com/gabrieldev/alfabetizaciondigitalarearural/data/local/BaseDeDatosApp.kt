package com.gabrieldev.alfabetizaciondigitalarearural.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.UsuarioDao

@Database(entities = [EntidadUsuario::class], version = 1, exportSchema = false)
abstract class BaseDeDatosApp : RoomDatabase() {
//implememntar dao
    abstract fun usuarioDao(): UsuarioDao
// singleton
    companion object {
        @Volatile
        private var INSTANCE: BaseDeDatosApp? = null

        fun obtenerBaseDeDatos(context: Context): BaseDeDatosApp {
            // Si ya existe, la retornamos
            return INSTANCE ?: synchronized(this)
            {
                // Si no, la creamos
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDeDatosApp::class.java,
                    "alfabetizacion_rural_db" // Nombre del archivo de la BD
                )
                    .fallbackToDestructiveMigration() // Si cambiamos la BD, borra todo y empieza de cero (útil para desarrollo)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}