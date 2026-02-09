package com.gabrieldev.alfabetizaciondigitalarearural.data.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.gabrieldev.alfabetizaciondigitalarearural.R
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

class NotificacionWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(
    context,
    workerParams
) {

    override fun doWork(): Result {
        Log.d("NotificacionWorker", "🔔 doWork() ejecutado!")
        mostrarNotificacion()
        return Result.success()
    }
    @SuppressLint("MissingPermission")
    private fun mostrarNotificacion() {
        val contexto = applicationContext

        Log.d("NotificacionWorker", "📱 Intentando mostrar notificación...")

        // Verificamos permiso (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    contexto,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e("NotificacionWorker", "❌ Permiso POST_NOTIFICATIONS no concedido")
                return
            }
        }
        val builder = NotificationCompat.Builder(contexto, "CANAL_ALFABETIZACION")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Es hora de aprender!")
            .setContentText("Continúa tu racha de aprendizaje hoy.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(contexto)) {
            notify(1001, builder.build())
            Log.d("NotificacionWorker", "✅ Notificación enviada con ID 1001")
        }
    }
}