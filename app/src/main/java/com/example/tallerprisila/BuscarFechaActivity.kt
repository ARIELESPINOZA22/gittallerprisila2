package com.example.tallerprisila

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class BuscarFechaActivity : AppCompatActivity() {

    private lateinit var btnFechaInicio: Button
    private lateinit var btnFechaFin: Button
    private lateinit var btnBuscar: Button
    private lateinit var listView: ListView
    private lateinit var txtTotalProformas: TextView


    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var fechaInicio: String? = null
    private var fechaFin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_fecha)

        btnFechaInicio = findViewById(R.id.btnFechaInicio)
        btnFechaFin = findViewById(R.id.btnFechaFin)
        btnBuscar = findViewById(R.id.btnBuscar)
        listView = findViewById(R.id.listViewResultados)
        txtTotalProformas = findViewById(R.id.txtTotalProformas)


        btnFechaInicio.setOnClickListener { mostrarDatePicker(true) }
        btnFechaFin.setOnClickListener { mostrarDatePicker(false) }

        btnBuscar.setOnClickListener {
            if (fechaInicio != null && fechaFin != null) {
                buscarProformasEntreFechas()
            } else {
                Toast.makeText(this, "Selecciona ambas fechas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDatePicker(esInicio: Boolean) {
        val calendario = Calendar.getInstance()
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            val fecha = String.format("%04d-%02d-%02d", y, m + 1, d)
            if (esInicio) {
                fechaInicio = fecha
                btnFechaInicio.text = "Inicio: $fecha"
            } else {
                fechaFin = fecha
                btnFechaFin.text = "Fin: $fecha"
            }
        }, year, month, day)

        datePicker.show()
    }

    private fun buscarProformasEntreFechas() {
        val resultados = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, resultados)
        listView.adapter = adapter

        db.collection("proformas")
            .whereGreaterThanOrEqualTo("fecha", fechaInicio!!)
            .whereLessThanOrEqualTo("fecha", fechaFin!!)
            .get()
            .addOnSuccessListener { documents ->
                resultados.clear()
                for (doc in documents) {
                    val cliente = doc.getString("cliente") ?: "Sin nombre"
                    val fecha = doc.getString("fecha") ?: ""
                    resultados.add("$cliente - $fecha")
                }
                adapter.notifyDataSetChanged()

                // Mostrar el total
                val total = resultados.size
                txtTotalProformas.text = "Total: $total proforma(s)"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al buscar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

