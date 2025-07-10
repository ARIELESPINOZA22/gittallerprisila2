package com.example.tallerprisila

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarProformaActivity : AppCompatActivity() {

    private lateinit var editCliente: EditText
    private lateinit var editObservaciones: EditText
    private lateinit var listViewItems: ListView
    private lateinit var btnGuardarCambios: Button

    private lateinit var proformaId: String
    private lateinit var adapter: ArrayAdapter<String>
    private val itemsList = mutableListOf<Item>()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_proforma)

        editCliente = findViewById(R.id.editTextCliente)
        editObservaciones = findViewById(R.id.editTextObservaciones)
        listViewItems = findViewById(R.id.listViewItems)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listViewItems.adapter = adapter

        proformaId = intent.getStringExtra("id") ?: return

        cargarDatos()

        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarDatos() {
        db.collection("proformas").document(proformaId)
            .get()
            .addOnSuccessListener { document ->
                editCliente.setText(document.getString("cliente"))
                editObservaciones.setText(document.getString("observaciones"))

                val itemsFirestore = document["items"] as? List<Map<String, Any>> ?: emptyList()
                itemsList.clear()

                for (itemMap in itemsFirestore) {
                    val descripcion = itemMap["descripcion"] as? String ?: ""
                    val cantidad = (itemMap["cantidad"] as? Long)?.toInt() ?: 0
                    val precio = itemMap["precio"] as? Double ?: 0.0
                    val item = Item(descripcion, cantidad, precio)
                    itemsList.add(item)
                }

                val itemStrings = itemsList.map { "${it.descripcion} (${it.cantidad} x Bs. ${it.precio})" }
                adapter.clear()
                adapter.addAll(itemStrings)
                adapter.notifyDataSetChanged()
            }
    }

    private fun guardarCambios() {
        val cliente = editCliente.text.toString()
        val observaciones = editObservaciones.text.toString()

        val nuevosDatos = mapOf(
            "cliente" to cliente,
            "observaciones" to observaciones,
            "items" to itemsList.map {
                mapOf(
                    "descripcion" to it.descripcion,
                    "cantidad" to it.cantidad,
                    "precio" to it.precio
                )
            }
        )

        db.collection("proformas").document(proformaId)
            .update(nuevosDatos)
            .addOnSuccessListener {
                Toast.makeText(this, "Proforma actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
    }
}
