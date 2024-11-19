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

        // se instancia con el this que es el contexto de la aplicacion
        networkStateReceiver = NetworkStateReceiver(this)

        // se llama al inicio de escucha para que vea si hay cambios
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
        networkStateReceiver.stopListening() // se llama ala funcion para que deje de escuchar
    }
}