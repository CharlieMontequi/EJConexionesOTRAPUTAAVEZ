package com.example.ejconexionesotraputaavez


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

class NetworkStateReceiver(private val appContext: Context) {

    // estableces un listener que comprueba si hay conexion
    private var listener: ((isConnected: Boolean, networkType: String) -> Unit)? = null

    //singleton del broadcast
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            // castea la conexion como gestos de coneciones a traves del paquete de servicios
            val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val isConnected: Boolean
            val networkType: String

            // se comprueba la compatabilidad de las versiones
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // version superior o igual a M

                // se instancia del gestor de conexiones con el activenetwork para saber que red esta activa
                val network = connectivityManager.activeNetwork

                // se recoge el tipo y caracteristicas de la red activa
                val capabilities = connectivityManager.getNetworkCapabilities(network)

                // se comprueba que la red esta activa
                isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

                // se comprueba las capabilities en funcion del tipo de red que usan(?)
                networkType = when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile"
                    else -> "Desconocida"
                }
            } else { // en caso de ser una version menor que M

                // instanciar la conexion
                val activeNetwork = connectivityManager.activeNetworkInfo

                // comprobar si existe conexion
                isConnected = activeNetwork?.isConnected == true

                // comprobar las caracteristicas de la red
                networkType = when (activeNetwork?.type) {
                    ConnectivityManager.TYPE_WIFI -> "WiFi"
                    ConnectivityManager.TYPE_MOBILE -> "Mobile"
                    else -> "Unknown"
                }
            }
            // se le asocia al listener la comprobacion de la conexion y el sacar el tipo de la misma
            listener?.invoke(isConnected, networkType)
        }
    }

    // se crea una funcion para que empiece a "esuchar" cambios en el tipo de conectividad
    fun startListening(onNetworkStateChanged: (isConnected: Boolean, networkType: String) -> Unit) {

        listener = onNetworkStateChanged // reacciona cuando se da un cambio
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        appContext.registerReceiver(receiver, filter) // Aquí se usa el contexto proporcionado
    }

    fun stopListening() {
        appContext.unregisterReceiver(receiver)
    }
}