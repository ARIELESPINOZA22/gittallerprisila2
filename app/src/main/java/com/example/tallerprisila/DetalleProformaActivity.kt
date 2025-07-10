package com.example.tallerprisila

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class DetalleProformaActivity : AppCompatActivity() {

    private lateinit var txtDetalle: TextView
    //declaracion de proformaId para boton editar
    private lateinit var proformaId: String

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_proforma)

        txtDetalle = findViewById(R.id.txtDetalle)

        val id = intent.getStringExtra("id")
        //asignacion de valor par el intent  para proformaId boton editar
        proformaId = intent.getStringExtra("id") ?: return


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


        //boton eliminar
        val btnEliminar: Button = findViewById(R.id.btnEliminar)
        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta proforma?")
                .setPositiveButton("Sí") { _, _ ->
                    val id = intent.getStringExtra("id")
                    if (id != null) {
                        FirebaseFirestore.getInstance().collection("proformas").document(id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Proforma eliminada", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


        //boton editar
        val btnEditar: Button = findViewById(R.id.btnEditarProforma)
        btnEditar.setOnClickListener {
            val intent = Intent(this, EditarProformaActivity::class.java)
            intent.putExtra("id", proformaId)
            startActivity(intent)
        }



    }

    fun volverAlInicio(view: android.view.View) {
        finish()
    }

    fun returnadd (view: View) {
        startActivity(Intent(this,MainActivity::class.java))
    }
}


