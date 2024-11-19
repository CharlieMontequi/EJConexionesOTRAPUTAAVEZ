package com.example.ejconexionesotraputaavez


import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var networkStateReceiver: NetworkStateReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val textViewConexicon = findViewById<TextView>(R.id.textConectar)

        networkStateReceiver = NetworkStateReceiver(this)
        networkStateReceiver.startListening { isConnected, networkType ->
            if (isConnected) {
                textViewConexicon.text = "${networkType.toString()}"
            } else {
                textViewConexicon.text = "Sin conexión a Internet"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver.stopListening() // Cancelar registro para evitar fugas de memoria
    }
}