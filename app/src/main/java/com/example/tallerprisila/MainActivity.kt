package com.example.tallerprisila

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.tallerprisila.Proforma
import  com.example.tallerprisila.Item

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
        btnGuardar.setOnClickListener {
            guardarProforma()
        }
    }

    private fun guardarProforma() {
        val items = listOf(
            Item("Soldadura de portón", 1, 850.0),
            Item("Pintura", 2, 50.0)
        )
        val total = items.sumOf { it.cantidad * it.precio }
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val proforma = Proforma(
            cliente = "ari",
            telefono = "71234567",
            fecha = fecha,
            items = items,
            total = total,
            observaciones = "Entrega en 3 días"
        )

        db.collection("proformas")
            .add(proforma)
            .addOnSuccessListener {
                Toast.makeText(this, "Proforma guardada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }
}
