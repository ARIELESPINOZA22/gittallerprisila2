package com.example.tallerprisila

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent


/*data class Item(val descripcion: String, val cantidad: Int, val precio: Double)
data class Proforma(
    val cliente: String,
    val telefono: String,
    val fecha: String,
    val items: List<Item>,
    val total: Double,
    val observaciones: String
)*/

class MainActivity : AppCompatActivity() {

    private lateinit var etCliente: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etItem: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etObservaciones: EditText
    private lateinit var btnAgregarItem: Button
    private lateinit var btnGuardar: Button
    private lateinit var tvItems: TextView

    private val listaItems = mutableListOf<Item>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCliente = findViewById(R.id.etCliente)
        etTelefono = findViewById(R.id.etTelefono)
        etItem = findViewById(R.id.etItem)
        etCantidad = findViewById(R.id.etCantidad)
        etPrecio = findViewById(R.id.etPrecio)
        etObservaciones = findViewById(R.id.etObservaciones)
        btnAgregarItem = findViewById(R.id.btnAgregarItem)
        btnGuardar = findViewById(R.id.btnGuardar)
        tvItems = findViewById(R.id.tvItems)

        btnAgregarItem.setOnClickListener { agregarItem() }
        btnGuardar.setOnClickListener { guardarProforma() }


        val btnVerProformas = findViewById<Button>(R.id.btnVerProformas)
        btnVerProformas.setOnClickListener {
            val intent = Intent(this, ProformasActivity::class.java)
            startActivity(intent)
        }


    }

    private fun agregarItem() {
        val descripcion = etItem.text.toString()
        val cantidad = etCantidad.text.toString().toIntOrNull()
        val precio = etPrecio.text.toString().toDoubleOrNull()

        if (descripcion.isNotBlank() && cantidad != null && precio != null) {
            val item = Item(descripcion, cantidad, precio)
            listaItems.add(item)
            tvItems.append("\n- ${item.descripcion}: ${item.cantidad} x Bs ${item.precio}")
            etItem.text.clear()
            etCantidad.text.clear()
            etPrecio.text.clear()
        } else {
            Toast.makeText(this, "Completa todos los campos del ítem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarProforma() {
        val cliente = etCliente.text.toString()
        val telefono = etTelefono.text.toString()
        val observaciones = etObservaciones.text.toString()
        val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val total = listaItems.sumOf { it.cantidad * it.precio }

        if (cliente.isBlank() || telefono.isBlank() || listaItems.isEmpty()) {
            Toast.makeText(this, "Faltan datos del cliente o ítems", Toast.LENGTH_SHORT).show()
            return
        }

        val proforma = Proforma(cliente, telefono, fecha, listaItems, total, observaciones)

        db.collection("proformas")
            .add(proforma)
            .addOnSuccessListener {
                Toast.makeText(this, "Proforma guardada con éxito", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limpiarCampos() {
        etCliente.text.clear()
        etTelefono.text.clear()
        etItem.text.clear()
        etCantidad.text.clear()
        etPrecio.text.clear()
        etObservaciones.text.clear()
        listaItems.clear()
        tvItems.text = "Ítems agregados:"
    }



}
