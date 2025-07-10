package com.example.tallerprisila

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ProformasActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val db = FirebaseFirestore.getInstance()
    private val proformasList = mutableListOf<String>()
    private val proformasIdList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proformas)

        listView = findViewById(R.id.listViewProformas)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, proformasList)
        listView.adapter = adapter

        db.collection("proformas")
            .get()
            .addOnSuccessListener { documents ->
                proformasList.clear()
                proformasIdList.clear()
                for (document in documents) {
                    val cliente = document.getString("cliente") ?: "Sin nombre"
                    val fecha = document.getString("fecha") ?: ""
                    proformasList.add("$cliente - $fecha")
                    proformasIdList.add(document.id)
                }
                adapter.notifyDataSetChanged()
            }

        listView.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, DetalleProformaActivity::class.java)
            intent.putExtra("id", proformasIdList[position])
            startActivity(intent)
        }
    }
}
