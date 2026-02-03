package com.gabrieldev.alfabetizaciondigitalarearural.data.remote

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.charset.StandardCharsets

class ManejadorNearby(
    private val context: Context
) {

    private val STRATEGY = Strategy.P2P_STAR
    private val SERVICE_ID = "com.gabrieldev.alfabetizaciondigitalarearural"
    private val gson = Gson()

    // estados de la interfaz
    private val _estadoConexion = MutableStateFlow("Desconectado")
    val estadoConexion = _estadoConexion.asStateFlow()

    private val _dispositivosEncontrados = MutableStateFlow<List<Endpoint>>(emptyList())
    val dispositivosEncontrados = _dispositivosEncontrados.asStateFlow()

    var onLeccionRecibida: ((LeccionTransferible) -> Unit)? = null

    data class Endpoint(val id: String, val nombre: String)

    // visibilidad a la espera
    fun hacerVisible(nombreUsuario: String) {
        val options = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        
        Nearby.getConnectionsClient(context)
            .startAdvertising(
                nombreUsuario, 
                SERVICE_ID, 
                connectionLifecycleCallback, 
                options
            )
            .addOnSuccessListener { _estadoConexion.value = "Visible como $nombreUsuario..." }
            .addOnFailureListener { _estadoConexion.value = "Error al iniciar publicidad: ${it.message}" }
    }

    // modo busqueda
    fun iniciarDescubrimiento() {
        _dispositivosEncontrados.value = emptyList() // Limpiar lista anterior
        val options = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()

        Nearby.getConnectionsClient(context)
            .startDiscovery(
                SERVICE_ID, 
                endpointDiscoveryCallback, 
                options
            )
            .addOnSuccessListener { _estadoConexion.value = "Buscando dispositivos..." }
            .addOnFailureListener { _estadoConexion.value = "Error al buscar: ${it.message}" }
    }

    fun conectarA(endpointId: String) {
        _estadoConexion.value = "Conectando..."
        // Pedimos conexión. "NombreEmisor" podría ser dinámico
        Nearby.getConnectionsClient(context)
            .requestConnection("Emisor", endpointId, connectionLifecycleCallback)
    }

    fun enviarLeccion(endpointId: String, datos: LeccionTransferible) {
        try {
            val json = gson.toJson(datos)
            val bytes = json.toByteArray(StandardCharsets.UTF_8)
            val payload = Payload.fromBytes(bytes)
            
            Nearby.getConnectionsClient(context).sendPayload(endpointId, payload)
            _estadoConexion.value = "Enviando lección..."
        } catch (e: Exception) {
            _estadoConexion.value = "Error al serializar: ${e.message}"
        }
    }

    fun detenerTodo() {
        Nearby.getConnectionsClient(context).stopAdvertising()
        Nearby.getConnectionsClient(context).stopDiscovery()
        Nearby.getConnectionsClient(context).stopAllEndpoints()
        _estadoConexion.value = "Desconectado"
        _dispositivosEncontrados.value = emptyList()
    }

    private var payloadPendiente: LeccionTransferible? = null

    //Detección de dispositivos (Solo para quien descubre)
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            val nuevo = Endpoint(endpointId, info.endpointName)
            _dispositivosEncontrados.value = _dispositivosEncontrados.value + nuevo
        }

        override fun onEndpointLost(endpointId: String) {
            _dispositivosEncontrados.value = _dispositivosEncontrados.value.filter { it.id != endpointId }
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
            _estadoConexion.value = "Conectando con ${info.endpointName}..."
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                _estadoConexion.value = "Conectado. Preparando envío..."

                payloadPendiente?.let { leccion ->
                    enviarLeccion(endpointId, leccion)
                    payloadPendiente = null
                }
            } else {
                _estadoConexion.value = "Conexión fallida"
                payloadPendiente = null
            }
        }

        override fun onDisconnected(endpointId: String) {
            _estadoConexion.value = "Desconectado"
        }
    }

    //Transferencia de datos
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val bytes = payload.asBytes() ?: return
                val json = String(bytes, StandardCharsets.UTF_8)
                try {
                    val leccionRecibida = gson.fromJson(json, LeccionTransferible::class.java)
                    onLeccionRecibida?.invoke(leccionRecibida)
                    _estadoConexion.value = "¡Lección recibida con éxito!"

                    Nearby.getConnectionsClient(context).disconnectFromEndpoint(endpointId)
                } catch (e: Exception) {
                    _estadoConexion.value = "Error al procesar datos recibidos"
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
        }
    }

    fun conectarYEnviar(endpointId: String, datos: LeccionTransferible) {
        payloadPendiente = datos
        conectarA(endpointId)
    }
}