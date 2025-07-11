package com.example.tallerprisila

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class BuscarClienteActivity : AppCompatActivity() {

    private lateinit var editTextBuscarCliente: EditText
    private lateinit var btnBuscarCliente: Button
    private lateinit var listViewResultados: ListView
    private val db = FirebaseFirestore.getInstance()
    private val resultados = mutableListOf<String>()
    private val proformasId = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_cliente)

        editTextBuscarCliente = findViewById(R.id.editTextBuscarCliente)
        btnBuscarCliente = findViewById(R.id.btnBuscarCliente)
        listViewResultados = findViewById(R.id.listViewResultados)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resultados)
        listViewResultados.adapter = adapter

        btnBuscarCliente.setOnClickListener {
            val clienteBuscado = editTextBuscarCliente.text.toString().trim()
            if (clienteBuscado.isNotEmpty()) {
                db.collection("proformas")
                    .whereEqualTo("cliente", clienteBuscado)
                    .get()
                    .addOnSuccessListener { documentos ->
                        resultados.clear()
                        proformasId.clear()
                        for (doc in documentos) {
                            val fecha = doc.getString("fecha") ?: ""
                            resultados.add("Cliente: $clienteBuscado - Fecha: $fecha")
                            proformasId.add(doc.id)
                        }
                        adapter.notifyDataSetChanged()
                    }
            } else {
                Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show()
            }
        }

        listViewResultados.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetalleProformaActivity::class.java)
            intent.putExtra("id", proformasId[position])
            startActivity(intent)
        }
    }
}
