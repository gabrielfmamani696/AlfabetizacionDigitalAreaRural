package com.gabrieldev.alfabetizaciondigitalarearural.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.LeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.UsuarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion

@Database(entities = [EntidadUsuario::class, EntidadLeccion::class], version = 2, exportSchema = false)
abstract class BaseDeDatosApp : RoomDatabase() {
    //implememntar dao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun leccionDao(): LeccionDao

    // singleton
    companion object {
        @Volatile
        private var INSTANCE: BaseDeDatosApp? = null

        fun obtenerBaseDeDatos(context: Context): BaseDeDatosApp {
            // Si ya existe, la retornamos
            return INSTANCE ?: synchronized(this)
            {
                // Sino la creamos
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDeDatosApp::class.java,
                    "alfabetizacion_rural_db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}