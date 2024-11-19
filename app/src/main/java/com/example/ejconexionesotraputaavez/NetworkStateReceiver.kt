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

    private var listener: ((isConnected: Boolean, networkType: String) -> Unit)? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val isConnected: Boolean
            val networkType: String

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                networkType = when {
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile"
                    else -> "Unknown"
                }
            } else {
                val activeNetwork = connectivityManager.activeNetworkInfo
                isConnected = activeNetwork?.isConnected == true
                networkType = when (activeNetwork?.type) {
                    ConnectivityManager.TYPE_WIFI -> "WiFi"
                    ConnectivityManager.TYPE_MOBILE -> "Mobile"
                    else -> "Unknown"
                }
            }
            listener?.invoke(isConnected, networkType)
        }
    }
    fun startListening(onNetworkStateChanged: (isConnected: Boolean, networkType: String) -> Unit) {
        listener = onNetworkStateChanged
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        appContext.registerReceiver(receiver, filter) // Aquí se usa el contexto proporcionado
    }

    fun stopListening() {
        appContext.unregisterReceiver(receiver)
    }
}