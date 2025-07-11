package com.example.tallerprisila

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

       val btnAdicionarCliente = findViewById<Button>(R.id.btnAdicionarCliente)
        val btnBusquedaCliente = findViewById<Button>(R.id.btnBusquedaCliente)
        val btnBusquedaFecha = findViewById<Button>(R.id.btnBusquedaFecha)

       btnAdicionarCliente.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnBusquedaCliente.setOnClickListener {
            startActivity(Intent(this, BuscarClienteActivity::class.java)) // asegúrate que esta actividad exista
        }

        btnBusquedaFecha.setOnClickListener {
            startActivity(Intent(this, BuscarFechaActivity::class.java)) // asegúrate que esta actividad exista
        }
    }

    fun irmainactivity (view: View) {
        startActivity(Intent(this,MainActivity::class.java))
    }
}
