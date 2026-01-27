package com.gabrieldev.alfabetizaciondigitalarearural.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.CuestionarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.IntentoLeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.LeccionDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.TarjetaDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.dao.UsuarioDao
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadCuestionario
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadIntentoLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadLeccion
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadPregunta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadRespuesta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadTarjeta
import com.gabrieldev.alfabetizaciondigitalarearural.data.local.entidades.EntidadUsuario

@Database(entities = [
    EntidadUsuario::class,
    EntidadLeccion::class,
    EntidadIntentoLeccion::class,
    EntidadTarjeta::class,
    EntidadCuestionario::class,
    EntidadPregunta::class,
    EntidadRespuesta::class
    ], version = 4, exportSchema = false)
abstract class BaseDeDatosApp : RoomDatabase() {
    //implememntar dao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun leccionDao(): LeccionDao
    abstract fun intentoLeccionDao(): IntentoLeccionDao
    abstract fun tarjetaDao(): TarjetaDao
    abstract fun cuestionarioDao(): CuestionarioDao

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
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}