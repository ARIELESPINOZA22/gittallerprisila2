package com.example.tallerprisila

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class DetalleProformaActivity : AppCompatActivity() {

    private lateinit var txtDetalle: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_proforma)

        txtDetalle = findViewById(R.id.txtDetalle)

        val id = intent.getStringExtra("id")

        if (id == null) {
            Toast.makeText(this, "ID no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("proformas").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val cliente = document.getString("cliente") ?: "Sin cliente"
                    val fecha = document.getString("fecha") ?: "Sin fecha"
                    val observaciones = document.getString("observaciones") ?: "Sin observaciones"
                    val total = document.getDouble("total") ?: 0.0

                    val itemsList = document.get("items") as? List<Map<String, Any>> ?: emptyList()

                    val itemsText = itemsList.joinToString("\n") { item ->
                        val desc = item["descripcion"] as? String ?: ""
                        val cant = (item["cantidad"] as? Long)?.toInt() ?: 0
                        val precio = item["precio"] as? Double ?: 0.0
                        "- $desc ($cant x Bs $precio)"
                    }

                    val detalle = """
                        Cliente: $cliente
                        Fecha: $fecha

                        Ítems:
                        $itemsText

                        Observaciones: $observaciones
                        Total: Bs $total
                    """.trimIndent()

                    txtDetalle.text = detalle
                } else {
                    Toast.makeText(this, "La proforma no existe", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener proforma", Toast.LENGTH_SHORT).show()
                Log.e("DetalleProforma", "Error al leer Firestore", e)
                finish()
            }
    }

    fun volverAlInicio(view: android.view.View) {
        finish()
    }

}


